/*
 * The MIT License
 *
 * Copyright (c) 2010 tap4j team (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tupilabs.tap4j;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.parboiled.common.Predicates;
import org.parboiled.support.Filters;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ToStringFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.parboiled.trees.GraphUtils.printTree;

/**
 * Test created to verify compatibility of new PEG parser with all
 * existing files in the test area.
 *
 * <p>Some files may have to be skipped if some feature is dropped
 * in the new parser, or if the file is used for bogus testing.</p>
 *
 * @since 5.0.0
 */
public class AllFilesTest {

    @TestFactory
    Stream<DynamicTest> dynamicTests() {
        final String[] ignore = new String[] {
                "consumer/issue3311330/fail_a.tap",
                "consumer/invalid_comment_tr_bailout_header.tap",
                "consumer/invalid_header_tr.tap",
                "consumer/invalid_plan_header_plan.tap",
                "consumer/invalid_plan_tr_header.tap",
                "consumer/invalid_tr.tap",
                "consumer/invalid_tr_footer.tap",
                "consumer/invalid_tr_header_header_tr.tap",
                "consumer/invalid_tr_plan_header.tap"
        };
        final String directory = Objects.requireNonNull(AllFilesTest.class.getResource("/org/tap4j/")).getFile();
        final Stream<File> files = FileUtils
                .listFiles(new File(directory), new String[] {"tap", "t"}, true)
                .stream()
                .filter(file -> Arrays.stream(ignore).noneMatch(ignored -> file.getAbsolutePath().endsWith(ignored)));
        return files
                .map(file -> DynamicTest.dynamicTest(
                        String.format("TAP PEG Parser regression test file [%s]", file.getName()),
                        () -> parse(Path.of(file.getAbsolutePath()), Boolean.parseBoolean(System.getenv("TAP_DEBUG")))
                        )
                );
    }

    private static void parse(Path path, boolean verbose) throws IOException {
        final String tapStream = Files.readString(path);
        Tap13Parser parser = new Tap13Parser();
        ParsingResult<Object> parsingResult = parser.parse(tapStream);
        if (parsingResult.hasErrors()) {
            if (verbose) {
                System.err.println("\n--- ParseErrors ---\n");
                // System.err.println(StringUtils.join(parsingResult.parseErrors, "---\n"));
                System.err.println(printParseErrors(parsingResult));
                System.err.println("\n--- ParseTree ---\n");
                System.err.println(printNodeTree(parsingResult, Filters.SKIP_EMPTY_OPTS_AND_ZOMS, Predicates.alwaysTrue()));
            }
            throw new RuntimeException(String.format("Errors parsing TAP file [%s]", path.getFileName()));
        } else if (!parsingResult.matched) {
            throw new RuntimeException("No match!");
        }
        if (verbose) {
            System.out.println("\n--- ParseTree ---\n");
            String tree = printTree(
                    parsingResult.parseTreeRoot,
                    new ToStringFormatter<>(),
                    Filters.SKIP_EMPTY_OPTS_AND_ZOMS,
                    Predicates.alwaysTrue()
            );
            System.out.println(tree);
        }
    }
}
