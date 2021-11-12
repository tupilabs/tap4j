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

    public static Rule EOL = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of("\r\n"))).label("Newline");

    public static Rule WS = new OneOrMoreMatcher(new AnyOfMatcher(Characters.of(" \t"))).label("Whitespace");

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
