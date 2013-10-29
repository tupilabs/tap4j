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
package org.tap4j.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Directive;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.SkipPlan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.model.Text;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.yaml.snakeyaml.Yaml;

/**
 * TAP 13 parser.
 *
 * @since 1.0
 */
public class Tap13Parser implements Parser {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Tap13Parser.class
            .getCanonicalName());

    /**
     * Stack of mementos. Each memento stores a current state of the parser.
     * When a new
     */
    private Stack<Memento> states = new Stack<Memento>();

    /**
     * The current state.
     */
    private Memento state = null;

    /**
     * Encoding used.
     */
    private String encoding;

    /**
     * Whether subtests are enabled or not.
     */
    private boolean subtestsEnabled = false;

    /**
     * Parser Constructor.
     *
     * @param encoding Encoding
     * @param enableSubtests Whether subtests are enabled or not
     */
    public Tap13Parser(String encoding, boolean enableSubtests) {
        super();
        this.encoding = encoding;
        this.subtestsEnabled = enableSubtests;
    }

    /**
     * Parser Constructor.
     *
     * @param enableSubtests Whether subtests are enabled or not
     */
    public Tap13Parser(boolean enableSubtests) {
        this("UTF-8", enableSubtests);
    }

    /**
     * Parser Constructor.
     */
    public Tap13Parser() {
        this("UTF-8", false);
    }

    /**
     * Saves the current state in the stack.
     */
    protected void pushMemento() {
        this.states.push(state);
        state = new Memento();
    }

    /**
     * Loads the previous state from the stack.
     */
    protected void popMemento() {
        this.state = this.states.pop();
    }

    /**
     * Get parser test set.
     * @return Test Set
     */
    protected TestSet getTestSet() {
        return state.getTestSet();
    }

    /**
     * {@inheritDoc}
     */
    public TestSet parseTapStream(String tapStream) {
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(tapStream.getBytes(encoding));
            return parse(is);
        } catch (UnsupportedEncodingException uee) {
            throw new ParserException("Invalid encoding: " + encoding, uee);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.log(
                            Level.SEVERE,
                            "Failed to close byte array stream: "
                                    + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public TestSet parseFile(File tapFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(tapFile);
            return parse(fis);
        } catch (FileNotFoundException e) {
            throw new ParserException("TAP file not found: " + tapFile, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close file stream: "
                            + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Internal parse routine.
     * @param stream Input Stream
     * @return Test Set
     */
    protected TestSet parse(InputStream stream) {
        state = new Memento();
        Scanner scanner = null;
        try {
            scanner = new Scanner(stream, this.encoding);
            String line = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line != null && line.trim().length() > 0) {
                    this.parseLine(line);
                }
            }
            this.onFinish();
        } catch (Exception e) {
            throw new ParserException("Error parsing TAP Stream: "
                    + e.getMessage(), e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return this.getTestSet();
    }

    /**
     * Parse a TAP line.
     *
     * @param tapLine TAP line
     */
    public void parseLine(String tapLine) {
        Matcher matcher = null;

        // Comment
        matcher = Constants.COMMENT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            onComment(matcher.group(1));
            return;
        }

        // Check if we already know the indentation level... if so, try to
        // find
        // out the indentation level of the current line in the TAP Stream.
        // If the line indentation level is greater than the pre-defined
        // one, than we know it is a) a META, b)
        if (state.getBaseIndentationLevel() > -1) {
            matcher = Constants.INDENTATION_PATTERN.matcher(tapLine);
            if (matcher.matches()) {
                int indentation = matcher.group(1).length();
                state.setCurrentIndentationLevel(indentation);
                if (indentation > state.getBaseIndentationLevel()) {
                    // we are at the start of the meta tags, but we should
                    // ignore
                    // the --- or ...
                    if (tapLine.trim().equals("---")) {
                        state.setCurrentlyInYaml(true);
                        return;
                    } else if (tapLine.trim().equals("...")) {
                        state.setCurrentlyInYaml(false);
                        return;
                    } else if (state.isCurrentlyInYaml()) {
                        this.appendTapLineToDiagnosticBuffer(tapLine);
                        return; // NOPMD by Bruno on 12/01/11 07:47
                    } else {
                        // If we are in a different level, but it is not
                        // YAML,
                        // Then it must be a subtest! Yay!
                        if (this.subtestsEnabled) {
                            // Check if we have some diagnostic set in the buffer
                            this.parseDiagnostics();
                            if (state.getLastParsedElement() instanceof TestResult) {
                                indentation = state.getBaseIndentationLevel();
                                TestResult lastTestResult = (TestResult) state.getLastParsedElement();
                                this.pushMemento();
                                lastTestResult.setSubtest(state.getTestSet());
                            } else if (state.getLastParsedElement() instanceof Plan) {
                                indentation = state.getBaseIndentationLevel();
                                Plan lastTestResult = (Plan) state.getLastParsedElement();
                                this.pushMemento();
                                lastTestResult.setSubtest(state.getTestSet());
                            }
                        }
                    }
                }

                // indentation cannot be less then the base indentation
                // level
                this.checkIndentationLevel(indentation, tapLine);
            }
        }

        // Check if we have some diagnostic set in the buffer
        this.parseDiagnostics();
        state.setLastLine(tapLine);

        // Bail Out
        matcher = Constants.BAIL_OUT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            onBailOut(matcher.group(1), matcher.group(3));
            return;
        }

        // Header
        matcher = Constants.HEADER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            onHeader(Integer.parseInt(matcher.group(1)), matcher.group(3));
            return;
        }

        // Plan
        matcher = Constants.PLAN_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            onPlan(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(3)), matcher.group(6),
                    matcher.group(8));
            return;
        }

        // Test Result
        matcher = Constants.TEST_RESULT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            String testNumberText = matcher.group(2);
            int testNumber = 0;
            if (testNumberText != null && testNumberText.trim().equals("") == false) { 
                testNumber = Integer.parseInt(testNumberText);
            } else {
                if (state.getTestSet().getPlan() != null && state.isPlanBeforeTestResult() == false)
                    return; // done testing mark
                if (state.getTestSet().getPlan() !=null && state.getTestSet().getPlan().getLastTestNumber() == state.getTestSet().getTestResults().size())
                    return; // done testing mark but plan before test result
                testNumber = getTestSet().getNextTestNumber();
            }
            onTestResult(StatusValues.get(matcher.group(1)),
                    testNumber, matcher.group(3),
                    DirectiveValues.get(matcher.group(5)),
                    matcher.group(6), matcher.group(8));
            return;
        }

        // Footer
        matcher = Constants.FOOTER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            onFooter(matcher.group(1), matcher.group(3));
            return;
        }

        // Any text. It should not be parsed by the consumer.
        final Text text = new Text(tapLine);
        getTestSet().getTapLines().add(text);
        state.setLastParsedElement(text);
    }

    /* -- Event handling -- */

    /**
     * Handles comments.
     *
     * @param text Comment
     */
    private void onComment(String text) {
        final Comment comment = new Comment(text);
        getTestSet().addComment(comment);

        if (state.getLastParsedElement() instanceof TestResult) {
            TestResult lastTestResult = (TestResult) state
                    .getLastParsedElement();
            lastTestResult.addComment(comment);
        }
    }

    /**
     * Handles Bail Out!s.
     *
     * @param reason Reason
     * @param comment Comment
     */
    private void onBailOut(String reason, String comment) {
        setIndentationLevelIfNotDefined(state.getLastLine());
        final BailOut bailOut = new BailOut(reason);
        if (comment != null && comment.trim().length() > 0) {
            bailOut.setComment(new Comment(comment, true));
        }
        getTestSet().addBailOut(bailOut);
        state.setLastParsedElement(getTestSet().getTapLines().get(
                (getTestSet().getTapLines().size() - 1)));
    }

    /**
     * Handles Headers.
     *
     * @param version Version
     * @param comment Comment
     */
    private void onHeader(int version, String comment) {
        if (getTestSet().getHeader() != null) {
            throw new ParserException("Duplicated TAP Header found.");
        }
        if (!state.isFirstLine()) {
            throw new ParserException(
                    "Invalid position of TAP Header. It must be the first "
                            + "element (apart of Comments) in the TAP Stream.");
        }
        setIndentationLevelIfNotDefined(state.getLastLine());
        state.setCurrentIndentationLevel(state.getBaseIndentationLevel());
        final Header header = new Header(version);
        if (comment != null && comment.trim().length() > 0) {
            header.setComment(new Comment(comment));
        }
        getTestSet().setHeader(header);
        state.setFirstLine(false);
        state.setLastParsedElement(header);
    }

    /**
     * Handles Plans.
     *
     * @param begin Plan begin
     * @param end Plan end
     * @param skip Skip
     * @param comment Comment
     */
    private void onPlan(int begin, int end, String skip, String comment) {
        if (getTestSet().getPlan() != null) {
            throw new ParserException("Duplicated TAP Plan found.");
        }
        if (getTestSet().getTestResults().size() <= 0
                && getTestSet().getBailOuts().size() <= 0) {
            state.setPlanBeforeTestResult(true);
        }
        setIndentationLevelIfNotDefined(state.getLastLine());
        Plan plan = new Plan(begin, end);

        if (skip != null && skip.trim().length() > 0) {
            plan.setSkip(new SkipPlan(skip));
        }

        if (comment != null && comment.trim().length() > 0) {
            plan.setComment(new Comment(comment));
        }
        getTestSet().setPlan(plan);
        state.setFirstLine(false);
        state.setLastParsedElement(plan);
    }

    /**
     * Handles Test Results.
     *
     * @param status Test Status
     * @param number Test Number
     * @param description Description
     * @param directive Directive
     * @param reason Reason
     * @param comment Comment
     */
    private void onTestResult(StatusValues status, int number, String description, DirectiveValues directive, 
            String reason, String comment) {
        setIndentationLevelIfNotDefined(state.getLastLine());
        final TestResult testResult = new TestResult(status, number);
        testResult.setDescription(description);

        if (directive != null) {
            testResult.setDirective(new Directive(directive, reason));
        }

        if (comment != null && comment.trim().length() > 0) {
            testResult.addComment(new Comment(comment));
        }
        getTestSet().addTestResult(testResult);
        state.setFirstLine(false);
        state.setLastParsedElement(testResult);
    }

    /**
     * Handles Footer.
     *
     * @param text Footer text
     * @param comment Comment
     */
    private void onFooter(String text, String comment) {
        final Footer footer = new Footer(text);
        if (comment != null && comment.trim().length() > 0) {
            footer.setComment(new Comment(comment, true));
        }
        getTestSet().setFooter(footer);
        state.setFirstLine(false);
    }

    /**
     * Called after the rest of the stream has been processed.
     */
    private void onFinish() {
        if (getTestSet().getPlan() == null) {
            throw new ParserException("Missing TAP Plan.");
        }
        parseDiagnostics();
        while (!this.states.isEmpty()) {
            this.popMemento();
        }
    }

    /* -- Utility methods --*/

    /**
     * <p>
     * Checks if there is any diagnostic information on the diagnostic buffer.
     * </p>
     * <p>
     * If so, tries to parse it using snakeyaml.
     * </p>
     */
    private void parseDiagnostics() {
        // If we found any meta, then process it with SnakeYAML
        if (state.getDiagnosticBuffer().length() > 0) {

            if (state.getLastParsedElement() == null) {
                throw new ParserException("Found diagnostic information without a previous TAP element.");
            }

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> metaIterable = (Map<String, Object>) new Yaml()
                        .load(state.getDiagnosticBuffer().toString());
                state.getLastParsedElement().setDiagnostic(metaIterable);
            } catch (Exception ex) {
                throw new ParserException("Error parsing YAML ["
                        + state.getDiagnosticBuffer().toString() + "]: "
                        + ex.getMessage(), ex);
            }
            this.state.getDiagnosticBuffer().setLength(0);
        }
    }

    /**
     * Appends a diagnostic line to diagnostic buffer. If the diagnostic line
     * contains --- or ... then it ignores this line. In the end of each line it
     * appends a break line.
     *
     * @param diagnosticLine diagnostic line
     */
    private void appendTapLineToDiagnosticBuffer(String diagnosticLine) {
        if (diagnosticLine.trim().equals("---") || diagnosticLine.trim().equals("...")) {
            return;
        }
        if (state.isCurrentlyInYaml()) {
            state.getDiagnosticBuffer().append(diagnosticLine);
            state.getDiagnosticBuffer().append('\n');
        }
    }

    /**
     * Set the indentation level, only if not defined yet.
     * @param tapLine TAP Line
     */
    private void setIndentationLevelIfNotDefined(String tapLine) {
        if (state.getBaseIndentationLevel() < 0) {
            state.setBaseIndentationLevel(this.getIndentationLevel(tapLine));
        }
    }

    /**
     * Get the indentation level of a line.
     *
     * @param tapLine line.
     * @return indentation level of a line.
     */
    private int getIndentationLevel(String tapLine) {
        int indentationLevel = 0;

        final Matcher indentMatcher = Constants.INDENTATION_PATTERN.matcher(tapLine);

        if (indentMatcher.matches()) {
            String spaces = indentMatcher.group(1);
            indentationLevel = spaces.length();
        }
        return indentationLevel;
    }

    /**
     * Checks if the indentation is greater than the
     * {@link #baseIndentationLevel}.
     *
     * @param indentation indentation level
     * @param tapLine TAP Line
     */
    private void checkIndentationLevel(int indentation, String tapLine) {
        if (indentation < state.getBaseIndentationLevel()) {
            if (!state.isCurrentlyInYaml()
                    && this.states.isEmpty() == Boolean.FALSE) {
                while (!this.states.isEmpty()
                        && indentation < state.getBaseIndentationLevel()) {
                    this.popMemento();
                }
            } else {
                throw new ParserException("Invalid indentation. "
                        + "Check your TAP Stream. Line: " + tapLine);
            }
        }
    }
}
