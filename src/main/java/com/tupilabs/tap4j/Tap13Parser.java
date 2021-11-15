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
                        // We need to use FirstOf here due to the Text rule of the Description consuming WS, which makes
                        // it harder to have Description WS Directive (since the WS was consumed as part of Description)
                        FirstOf(
                                // Description + Directive
                                Sequence(
                                        WS,
                                        Text().label("Description"),
                                        Directive()
                                ),
                                // Description
                                Sequence(
                                        WS,
                                        Text().label("Description")
                                ),
                                // Directive
                                Sequence(
                                        WS,
                                        Directive()
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
                        Text().label("Description")
                )
        );
    }

    Rule BailOut() {
        return Sequence(
                String("Bail out!").label("Bail Out Constant"),
                Optional(
                        Sequence(
                                WS,
                                Text().label("Reason")
                        )
                ),
                EOL
        ).label("Bail Out Line");
    }

    Rule Diagnostics () {
        return Sequence(
                "#",
                Optional(
                        Text().label("Diagnostic")
                ),
                EOL
        ).label("Diagnostics Line");
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

    public static final Rule WS = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of(" \t\f"))).label("Whitespace");

    // --- Others

    // we redefine the rule creation for string literals to automatically match trailing whitespace if the string
    // literal ends with a space character, this way we don't have to insert extra whitespace() rules after each
    // character or string literal

//    @Override
//    protected Rule fromStringLiteral(String string) {
//        return string.endsWith(" ") ?
//                Sequence(String(string.substring(0, string.length() - 1)), WS) :
//                String(string);
//    }

    // --- main method for testing

    public static void main(String[] args) {
        String input = "TAP Version 13\n" +
                "1..20\n" +
                "ok 1 the description is here\n" +
                "not ok\n" +
                "not ok 13 # TODO bend space and time\n" +
                "not ok 13 # todo bend space and time again\n" +
                "ok 23 test # skip Insufficient amount pressure.\n" +
                "not ok 13 # TODO bend space and time\n" +
                "ok 23 # skip Insufficient amount pressure.\n" +
                "Bail out! MySQL is not running.\n" +
                "#\n" +
                "# Create a new Board and Tile, then place\n" +
                "# the Tile onto the board.\n" +
                "#\n" +
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
