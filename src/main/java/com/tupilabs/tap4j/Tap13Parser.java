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
import org.parboiled.matchers.ZeroOrMoreMatcher;
import org.parboiled.parserunners.BasicParseRunner;
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
                Lines(),
                ZeroOrMore(EOL), // FIXME: is there a way in parboiled to ignore the multiline EOL?
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
                EOL
        ).label("Plan");
    }

    Rule Lines() {
        return ZeroOrMore(
                FirstOf(
                        TestLine(),
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

    Rule Directive() {
        return Sequence(
                String('#').label("Hash"),
                WS,
                FirstOf("skip", "SKIP", "todo", "TODO").label("Directive Type"),
                Optional(
                        WS,
                        Text().label("Directive Description")
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
                        Text().label("Diagnostic")
                ),
                EOL
        ).label("Diagnostics Line");
    }

    Rule Comment() {
        return Sequence(
                String('#').label("Hash"),
                Optional(
                        OneOrMore(
                                Sequence(
                                        TestNot(AnyOf("\r\n")),
                                        ANY
                                )
                        )
                                .suppressSubnodes()
                                .label("Comment Text")
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

    // --- Terminals

    public static final Rule EOL = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of("\r\n"))).label("Newline");

    public static final Rule WS = new ZeroOrMoreMatcher(new AnyOfMatcher(Characters.of(" \t\f"))).label("Whitespace");

    // --- Others

    private String preprocess(String input) {
        return input
                // replace multiple spaces by single space
                .replaceAll(" +", " ")
                // replace trailing space
                .replaceAll(" \n", "\n")
                .replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public ParsingResult<Object> parse(String input) {
        Tap13Parser parser = new Tap13Parser();
        return new BasicParseRunner<>(parser.TestSet()).run(preprocess(input));
    }

    // --- main method for testing

    public static void main(String[] args) {
        String input = "TAP Version 13\n" +
                "1..20\n" +
                "ok 1 \n" +
                "not ok 2 \n" +
                "ok 3\n" +
                "not ok\n" +
                "\n" +
                "\n" +
                "   \n" +
                // "   ok 4" + // invalid!
                "ok 5 this is a test\n" +
                "not ok also this!\n" +
                "ok 7 # TODO failed!\n" +
                "ok 8 description with # SKIP ah\n" +
                "not ok 9 # always fails\n" +
                "ok 10 # always fails 2 \n" +
                "ok #\n" +
                "ok # \n" +
                "ok  #  \n" +
                "ok 14 Test # failed Class#method \n" +
                "Bail out!\n" +
                "Bail out!   \n" +
                "Bail out! Some reason\n" +
                "Bail out! Some reason # with a comment\n" +
                // "not ok # a comment\n" +
//                "not ok 13 # TODO bend space and time\n" +
//                "not ok 13 # todo bend space and time again\n" +
//                "ok 23 test # skip Insufficient amount pressure.\n" +
//                "not ok 13 # TODO bend space and time\n" +
//                "ok 23 # skip Insufficient amount pressure.\n" +
//                "Bail out! MySQL is not running.\n" +
//                "#\n" +
//                "# Create a new Board and Tile, then place\n" +
//                "# the Tile onto the board.\n" +
                "";
        Tap13Parser parser = new Tap13Parser();
        ParsingResult<Object> parsingResult = parser.parse(input);
        // ParsingResult<Object> parsingResult = new BasicParseRunner<>(parser.TestSet()).run(input);
        if (parsingResult.hasErrors()) {
            System.err.println("\n--- ParseErrors ---\n");
            System.err.println(StringUtils.join(parsingResult.parseErrors, "---\n"));
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
