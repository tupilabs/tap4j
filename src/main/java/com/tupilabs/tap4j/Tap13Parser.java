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

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.buffers.IndentDedentInputBuffer;
import org.parboiled.common.Predicates;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.matchers.ZeroOrMoreMatcher;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.Characters;
import org.parboiled.support.Filters;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ToStringFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.parboiled.trees.GraphUtils.printTree;

/**
 * A TAP 13 parser, that uses a PEG grammar to parse TAP files,
 * producing a parse tree.
 *
 * <p>The parser objects created by parboiled are not thread-safe.</p>
 */
@BuildParseTree
public class Tap13Parser extends BaseParser<Object> {

    Rule TestSet() {
        return Sequence(
                Optional(Version()),
                FirstOf(
                        Sequence(
                                Plan(),
                                Lines()
                        ),
                        Sequence(
                                Lines(),
                                Plan()
                        )
                ),
                EOI
        ).label("Test Set");
    }

    // --- TAP Rules

    Rule Version() {
        return Sequence(
                IgnoreCase("TAP Version 13"),
                EOL
        ).label("Version");
    }

    Rule Plan() {
        return Sequence(
                String("1.."),
                Number(),
                Optional(
                        Sequence(
                                WS,
                                Comment()
                        )
                ),
                EOL
        ).label("Plan");
    }

    Rule Lines() {
        return ZeroOrMore(
                FirstOf(
                        TestLine(),
                        YamlBlock(),
                        BailOut(),
                        Diagnostics()
                )
        ).label("Lines");
    }

    Rule TestLine() {
        return Sequence(
                TestStatus(),
                Optional(
                        Sequence(
                                WS,
                                TestNumber()
                        )
                ),
                Optional(
                        // Description
                        Sequence(
                                WS,
                                Text().label("Description")
                        )
                ),
                Optional(
                        FirstOf(
                                // Directive
                                Sequence(
                                        WS,
                                        Directive()
                                ),
                                // Comment
                                Sequence(
                                        WS,
                                        Comment()
                                )
                        )
                ),
                EOL
        );
    }

    Rule TestStatus() {
        return FirstOf("ok", "not ok").label("Test Status");
    }

    Rule TestNumber() {
        return Number().label("Test Number");
    }

    Rule YamlBlock() {
        return Sequence(
                INDENT,
                String("---").label("Yaml Block Start"),
                EOL,
                ZeroOrMore(
                        YamlLine()
                ).suppressSubnodes(),
                String("...").label("Yaml Block End"),
                EOL,
                DEDENT
        );
    }

    Rule YamlLine() {
        return OneOrMore(
                Sequence(
                        TestNot(String("...")),
                        ANY
                )
        )
                .suppressSubnodes()
                .label("Any Text");
    }

    Rule Directive() {
        return Sequence(
                String('#').label("Hash"),
                WS,
                FirstOf("skip", "SKIP", "todo", "TODO").label("Directive Type"),
                Optional(
                        WS,
                        AnyText().label("Directive Description")
                )
        );
    }

    Rule BailOut() {
        return Sequence(
                String("Bail out!").label("Bail Out Constant status"),
                Optional(
                        Sequence(
                                WS,
                                Text().label("Reason")
                        )
                ),
                Optional(
                        Sequence(
                                WS,
                                Comment()
                        )
                ),
                EOL
        ).label("Bail Out Line");
    }

    Rule Diagnostics() {
        return Sequence(
                String('#').label("Hash"),
                Optional(
                        AnyText().label("Diagnostic")
                ),
                EOL
        ).label("Diagnostics Line");
    }

    Rule Comment() {
        return Sequence(
                String('#').label("Hash"),
                Optional(
                        AnyText().label("Comment Text")
                )
        ).label("Comment");
    }

    // --- Support rules

    Rule Number() {
        return OneOrMore(CharRange('0', '9').label("Digit")).label("Number");
    }

    Rule Text() {
        return OneOrMore(
                Sequence(
                        TestNot(AnyOf("\r\n#")),
                        ANY
                )
        )
                .suppressSubnodes()
                .label("Text");
    }

    Rule AnyText() {
        return OneOrMore(
                Sequence(
                        TestNot(AnyOf("\r\n")),
                        ANY
                )
        )
                .suppressSubnodes()
                .label("Any Text");
    }

    // --- Terminals

    public static final Rule EOL = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of("\r\n"))).label("Newline");

    public static final Rule WS = new ZeroOrMoreMatcher(new AnyOfMatcher(Characters.of(" \t\f"))).label("Whitespace");

    // --- Others

    /**
     * Preprocesses the input stream, doing the following:
     *
     * <ul>
     * <li>Replaces multiple spaces by a single one.</li>
     * <li>Removes trailing spaces.</li>
     * <li>Removes empty lines.</li>
     * </ul>
     *
     * @param input the input stream
     * @return a String with no empty lines, no trailing spaces, and only single spaces.
     */
    private String preprocess(String input) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        // replace multiple spaces by single space
        try (BufferedReader br = new BufferedReader(new StringReader(input))) {
            String line;
            while((line = br.readLine()) != null) {
                if (!line.startsWith(" ")) {
                    buffer.append(line.replaceAll(" +", " "));
                    buffer.append(System.lineSeparator());
                } else {
                    buffer.append(line);
                    buffer.append(System.lineSeparator());
                }
            }
        }
        return buffer.toString()
                // remove trailing space
                .replaceAll(" \n", "\n")
                // remove empty lines
                .replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    private int guessIndentation(String input) {
        // FIXME: guess the indentation from the first two spaces or more found
        return 2;
    }

    public ParsingResult<Object> parse(String input) throws IOException {
        final String preprocessedInput = preprocess(input);
        final int indentation = guessIndentation(preprocessedInput);
        Tap13Parser parser = new Tap13Parser();
        // return new BasicParseRunner<>(parser.TestSet()).run(new IndentDedentInputBuffer(preprocessedInput.toCharArray(), indentation, /* comments chart */ null, /* strict */ false));
        return new ReportingParseRunner<>(parser.TestSet()).run(new IndentDedentInputBuffer(preprocessedInput.toCharArray(), indentation, /* comments chart */ null, /* strict */ false));
    }

    // --- main method for testing

    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of("/home/kinow/Development/java/workspace/tap4j/src/test/resources/org/tap4j/parser/issueFalseDupPlan/ihavetodoes.tap"));
        Tap13Parser parser = new Tap13Parser();
        ParsingResult<Object> parsingResult = parser.parse(input);
        if (parsingResult.hasErrors()) {
            System.err.println("\n--- ParseErrors ---\n");
            // System.err.println(StringUtils.join(parsingResult.parseErrors, "---\n"));
            System.err.println(printParseErrors(parsingResult));
            System.err.println("\n--- ParseTree ---\n");
            System.err.println(printNodeTree(parsingResult, Filters.SKIP_EMPTY_OPTS_AND_ZOMS, Predicates.alwaysTrue()));
        } else if (!parsingResult.matched) {
            System.err.println("\n--- No Matches! ---\n");
        } else {
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
