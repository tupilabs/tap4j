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
import org.parboiled.common.Predicates;
import org.parboiled.common.StringUtils;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.OneOrMoreMatcher;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.Characters;
import org.parboiled.support.Filters;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ToStringFormatter;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.parboiled.trees.GraphUtils.printTree;

/**
 * A TAP 13 parser, that uses a PEG grammar to parse TAP files,
 * producing a parse tree.
 */
@BuildParseTree
public class Tap13Parser extends BaseParser<Object> {

    Rule TestSet() {
        return Sequence(
                Version(),
                Plan(),
                TestLines(),
                EOI
        ).label("Test Set");
    }

    // --- TAP Rules

    Rule Version() {
        return Sequence(
                String("TAP Version 13"),
                EOL
        ).label("Version");
    }

    Rule Plan() {
        return Sequence(
                String("1.."),
                Number(),
                Optional(EOL)
        ).label("Plan");
    }

    Rule TestLines() {
        return ZeroOrMore(TestLine()).label("Test Lines");
    }

    Rule TestLine() {
        return Sequence(
                TestStatus(),
                Optional(
                        WS,
                        TestNumber()),
                Optional(
                        WS,
                        Text().label("Description")
                ),
                Optional(EOL)
        );
    }

    Rule TestStatus() {
        return FirstOf(String("ok"), String("not ok")).label("Test Status");
    }

    Rule TestNumber() {
        return Number().label("Test Number");
    }

    // --- Support rules

    Rule Number() {
        return OneOrMore(CharRange('0', '9').label("Digit")).label("Number");
    }

    Rule Text() {
        return ZeroOrMore(
            Sequence(
                    TestNot(AnyOf("\r\n#")),
                    ANY
            )
                    .suppressSubnodes()
                    .label("Text")
        );
    }

    // --- Terminals

    public static final Rule EOL = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of("\r\n"))).label("Newline");

    public static final Rule WS = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of(" \t"))).label("Whitespace");

    // --- main method for testing

    public static void main(String[] args) {
        String input = "TAP Version 13\n" +
                "1..20\n" +
                "ok 1 the description is here\n" +
                "not ok\n" +
                // FIXME: continue here, add directives
//                "not ok 13 # TODO bend space and time\n" +
//                "ok 23 # skip Insufficient flogiston pressure.\n" +
                "";
        Tap13Parser parser = new Tap13Parser();
        ParsingResult<Object> parsingResult = new TracingParseRunner<>(parser.TestSet()).run(input);
        // ParsingResult<Object> parsingResult = new BasicParseRunner<>(parser.TestSet()).run(input);
        if (parsingResult.hasErrors()) {
            System.err.println("\n--- ParseErrors ---\n");
            System.err.println(StringUtils.join(parsingResult.parseErrors, "---\n"));
            System.err.println("\n--- ParseTree ---\n");
            System.err.println(printNodeTree(parsingResult, Filters.SKIP_EMPTY_OPTS_AND_ZOMS, Predicates.alwaysTrue()));
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
