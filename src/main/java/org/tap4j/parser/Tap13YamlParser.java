/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Directive;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.SkipPlan;
import org.tap4j.model.TapElement;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.model.Text;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.yaml.snakeyaml.Yaml;

/**
 * TAP 13 parser with support to YAML.
 * 
 * @since 1.0
 */
public class Tap13YamlParser implements Parser {

    protected static final Pattern INDENTANTION_PATTERN = Pattern
        .compile("((\\s|\\t)*)?.*");

    private TestSet testSet;

    private Stack<Memento> mementos = new Stack<Memento>();

    private boolean firstLine;

    private boolean planBeforeTestResult;

    private boolean currentlyInYAML;

    // Helper String to check the Footer
    private String lastLine = null;

    private TapElement lastParsedElement;

    /**
     * Indicator of the base indentation level. Usually defined by the TAP
     * Header.
     */
    private int baseIndentationLevel;

    /**
     * Helper indicator of in what indentantion level we are working at moment.
     * It is helpful specially when you have many nested elements, like a META
     * element with some multiline text.
     */
    private int currentIndentationLevel;

    /**
     * YAML parser and emitter.
     */
    private Yaml yaml;

    private StringBuilder diagnosticBuffer;

    public Tap13YamlParser() {
        super();
        this.init();
    }

    /**
     * Called from the constructor and everytime a new TAP Stream (file or
     * string) is processed.
     */
    public final void init() {
        this.baseIndentationLevel = -1;
        this.currentIndentationLevel = -1;
        this.currentlyInYAML = Boolean.FALSE;
        this.diagnosticBuffer = new StringBuilder();
        this.lastParsedElement = null;
        this.firstLine = Boolean.TRUE;
        this.planBeforeTestResult = Boolean.FALSE;
        this.testSet = new TestSet();
        yaml = new Yaml();
    }

    /**
     * Save the parser memento.
     */
    private void saveMemento() {
        Memento memento = new Memento();
        memento.setBaseIndentationLevel(this.baseIndentationLevel);
        memento.setCurrentIndentationLevel(this.currentIndentationLevel);
        memento.setCurrentlyInYaml(this.currentlyInYAML);
        memento.setDiagnosticBuffer(this.diagnosticBuffer);
        memento.setLastParsedElement(this.lastParsedElement);
        memento.setFirstLine(this.firstLine);
        memento.setPlanBeforeTestResult(this.planBeforeTestResult);
        memento.setTestSet(this.testSet);
        this.mementos.push(memento);
    }

    /**
     * Load the parser memento.
     */
    private void loadMemento() {
        Memento memento = this.mementos.pop();
        this.baseIndentationLevel = memento.getBaseIndentationLevel();
        this.currentIndentationLevel = memento.getCurrentIndentationLevel();
        this.currentlyInYAML = memento.isCurrentlyInYaml();
        this.diagnosticBuffer = memento.getDiagnosticBuffer();
        this.lastParsedElement = memento.getLastParsedElement();
        this.firstLine = memento.isFirstLine();
        this.planBeforeTestResult = memento.isPlanBeforeTestResult();
        this.testSet = memento.getTestSet();
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.TapConsumer#getTestSet()
     */
    public TestSet getTestSet() {
        return this.testSet;
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.consumer.DefaultTapConsumer#parseLine(java.lang.String)
     */
    public void parseLine(String tapLine) {
        Matcher matcher = null;

        // Comment
        matcher = COMMENT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractComment(matcher);
            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Last line that is not a comment.
        lastLine = tapLine;

        // Check if we already know the indentation level... if so, try to find
        // out the indentation level of the current line in the TAP Stream.
        // If the line indentation level is greater than the pre-defined
        // one, than we know it is a) a META, b)
        if (this.isBaseIndentationAlreadyDefined()) {
            matcher = INDENTANTION_PATTERN.matcher(tapLine);
            if (matcher.matches()) {
                String spaces = matcher.group(1);
                int indentation = spaces.length();
                this.currentIndentationLevel = indentation;
                if (indentation > this.baseIndentationLevel) {
                    // we are at the start of the meta tags, but we should
                    // ignore
                    // the --- or ...
                    // TBD: check how snakeyaml can handle these tokens.
                    if (tapLine.trim().equals("---")) {
                        this.currentlyInYAML = true;
                        return;
                    } else if (tapLine.trim().equals("...")) {
                        this.currentlyInYAML = false;
                        return;
                    } else if (this.currentlyInYAML) {
                        this.appendTapLineToDiagnosticBuffer(tapLine);
                        return; // NOPMD by Bruno on 12/01/11 07:47
                    } else {
                        // If we are in a different level, but it is not YAML,
                        // Then it must be a subtest! Yay!
                        if (this.lastParsedElement instanceof TestResult) {
                            indentation = baseIndentationLevel;
                            TestResult lastTestResult = (TestResult) this.lastParsedElement;
                            TestSet newTestSet = new TestSet();
                            lastTestResult.setSubtest(newTestSet);
                            this.saveMemento();
                            this.init();
                            this.testSet = newTestSet;
                        }
                    }
                }

                // indentation cannot be less then the base indentation level
                this.checkIndentationLevel(indentation, tapLine);
            }
        }

        // Check if we have some diagnostic set in the buffer
        this.checkAndParseTapDiagnostic();

        // Header
        matcher = HEADER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.setIndentationLevelIfNotDefined(tapLine);

            this.currentIndentationLevel = this.baseIndentationLevel;

            this.checkTAPHeaderParsingLocationAndDuplicity();

            this.extractHeader(matcher);
            this.firstLine = false;

            this.lastParsedElement = this.testSet.getHeader();

            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Check if the header was set
        // this.checkHeader();

        // Plan
        matcher = PLAN_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.checkTAPPlanDuplicity();

            this.checkIfTAPPlanIsSetBeforeTestResultsOrBailOut();

            this.setIndentationLevelIfNotDefined(tapLine);

            this.extractPlan(matcher);
            this.firstLine = false;

            this.lastParsedElement = this.testSet.getPlan();

            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Test Result
        matcher = TEST_RESULT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.setIndentationLevelIfNotDefined(tapLine);

            this.extractTestResult(matcher);

            this.lastParsedElement = this.testSet.getTapLines()
                .get((this.testSet.getTapLines().size() - 1));

            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Bail Out
        matcher = BAIL_OUT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {

            this.setIndentationLevelIfNotDefined(tapLine);

            this.extractBailOut(matcher);

            this.lastParsedElement = this.testSet.getTapLines()
                .get((this.testSet.getTapLines().size() - 1));

            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Footer
        matcher = FOOTER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractFooter(matcher);

            this.lastParsedElement = this.testSet.getFooter();

            return; // NOPMD by Bruno on 12/01/11 07:47
        }

        // Any text. It should not be parsed by the consumer.
        final Text text = new Text(tapLine);
        this.lastParsedElement = text;
        this.testSet.addTapLine(text);

    }

    /**
     * Checks if the TAP Plan is set before any Test Result or Bail Out.
     */
    protected void checkIfTAPPlanIsSetBeforeTestResultsOrBailOut() {
        if (this.testSet.getTestResults().size() <= 0 &&
            this.testSet.getBailOuts().size() <= 0) {
            this.planBeforeTestResult = true;
        }
    }

    /**
     * Checks the Header location and duplicity. The Header must be the first
     * element and cannot occurs more than on time. However the Header is
     * optional.
     */
    protected void checkTAPHeaderParsingLocationAndDuplicity() {
        if (this.testSet.getHeader() != null) {
            throw new ParserException("Duplicated TAP Header found.");
        }
        if (!firstLine) {
            throw new ParserException(
                                      "Invalid position of TAP Header. It must be the first element (apart of Comments) in the TAP Stream.");
        }
    }

    /**
     * Checks if there are more than one TAP Plan in the TAP Stream.
     */
    protected void checkTAPPlanDuplicity() {
        if (this.testSet.getPlan() != null) {
            throw new ParserException("Duplicated TAP Plan found.");
        }
    }

    /**
     * This method is called after the TAP Stream has already been parsed. So we
     * just check if the plan was found before test result or bail outs. If so,
     * skip this check. Otherwise, we shall check if the last line is the TAP
     * Plan.
     * 
     * @deprecated
     */
    protected void checkTAPPlanPosition() {
        if (!this.planBeforeTestResult) {
            Matcher matcher = PLAN_PATTERN.matcher(lastLine);

            if (matcher.matches()) {
                return; // OK
            }

            throw new ParserException("Invalid position of TAP Plan.");
        }
    }

    /**
     * Checks if TAP Plan has been set.
     * 
     * @throws ParserException if TAP Plan has not been set.
     */
    protected void checkTAPPlanIsSet() {
        if (this.testSet.getPlan() == null) {
            throw new ParserException("Missing TAP Plan.");
        }
    }

    /**
     * Extracts the Header from a TAP Line.
     * 
     * @param matcher REGEX Matcher.
     */
    protected void extractHeader(Matcher matcher) {
        final Integer version = Integer.parseInt(matcher.group(1));

        final Header header = new Header(version);

        final String commentToken = matcher.group(2);

        if (commentToken != null) {
            String text = matcher.group(3);
            final Comment comment = new Comment(text);
            header.setComment(comment);
        }

        this.testSet.setHeader(header);
    }

    /**
     * @param matcher REGEX Matcher.
     */
    protected void extractPlan(Matcher matcher) {
        Integer initialTest = Integer.parseInt(matcher.group(1));
        Integer lastTest = Integer.parseInt(matcher.group(3));

        Plan plan = null;
        plan = new Plan(initialTest, lastTest);

        String skipToken = matcher.group(4);
        if (skipToken != null) {
            String reason = matcher.group(5);
            final SkipPlan skip = new SkipPlan(reason);
            plan.setSkip(skip);
        }

        String commentToken = matcher.group(6);
        if (commentToken != null) {
            String text = matcher.group(7);
            final Comment comment = new Comment(text);
            plan.setComment(comment);
        }

        this.testSet.setPlan(plan);
    }

    /**
     * @param matcher REGEX Matcher.
     */
    protected void extractTestResult(Matcher matcher) {
        TestResult testResult = null;

        final String okOrNotOk = matcher.group(1);
        StatusValues status = null;
        if (okOrNotOk.trim().equals("ok")) {
            status = StatusValues.OK;
        } else // regex mate...
        {
            status = StatusValues.NOT_OK;
        }

        Integer testNumber = this.getTestNumber(matcher.group(2));

        testResult = new TestResult(status, testNumber);

        testResult.setDescription(matcher.group(3));

        String directiveToken = matcher.group(4);
        if (directiveToken != null) {
            String directiveText = matcher.group(5);
            DirectiveValues directiveValue = null;
            if (directiveText.trim().equalsIgnoreCase("todo")) {
                directiveValue = DirectiveValues.TODO;
            } else {
                directiveValue = DirectiveValues.SKIP;
            }
            String reason = matcher.group(6);
            Directive directive = new Directive(directiveValue, reason);
            testResult.setDirective(directive);
        }

        String commentToken = matcher.group(7);
        if (commentToken != null) {
            String text = matcher.group(8);
            final Comment comment = new Comment(text);
            comment.setInline(Boolean.TRUE);
            testResult.addComment(comment);
        }

        this.testSet.addTestResult(testResult);
    }

    /**
     * Returns the test number out from an input String. If the string is null
     * or equals "" this method returns the next test result number. Otherwise
     * it will return the input String value parsed to an Integer.
     * 
     * @param testNumber
     * @return
     */
    private Integer getTestNumber(String testNumber) {
        Integer integerTestNumber = null;
        if (StringUtils.isEmpty(testNumber)) {
            integerTestNumber = (this.testSet.getTestResults().size() + 1);
        } else {
            integerTestNumber = Integer.parseInt(testNumber);
        }
        return integerTestNumber;
    }

    /**
     * @param matcher REGEX Matcher.
     */
    protected void extractBailOut(Matcher matcher) {
        String reason = matcher.group(1);

        BailOut bailOut = new BailOut(reason);

        String commentToken = matcher.group(2);

        if (commentToken != null) {
            String text = matcher.group(3);
            Comment comment = new Comment(text);
            bailOut.setComment(comment);
        }

        this.testSet.addBailOut(bailOut);
    }

    /**
     * @param matcher REGEX Matcher.
     */
    protected void extractComment(Matcher matcher) {
        String text = matcher.group(1);
        Comment comment = new Comment(text);

        this.testSet.addComment(comment);

        if (lastParsedElement instanceof TestResult) {
            TestResult lastTestResult = (TestResult) lastParsedElement;
            lastTestResult.addComment(comment);
        }
    }

    /**
     * Simply extracts the footer from the TAP line.
     * 
     * @param matcher REGEX Matcher.
     */
    protected void extractFooter(Matcher matcher) {
        String text = matcher.group(1);
        Footer footer = new Footer(text);

        final String commentToken = matcher.group(2);

        if (commentToken != null) {
            String commentText = matcher.group(3);
            final Comment comment = new Comment(commentText);
            footer.setComment(comment);
        }

        this.testSet.setFooter(footer);
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.TapConsumer#parseTapStream(java.lang.String)
     */
    public TestSet parseTapStream(String tapStream) {

        this.init();

        Scanner scanner = null;

        try {
            scanner = new Scanner(tapStream);
            String line = null;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (StringUtils.isNotEmpty(line)) {
                    this.parseLine(line);
                }
            }
            this.postProcess();
        } catch (Exception e) {
            throw new ParserException("Error parsing TAP Stream: " +
                                      e.getMessage(), e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return this.getTestSet();

    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.TapConsumer#parseFile(java.io.File)
     */
    public TestSet parseFile(File tapFile) {

        this.init();

        Scanner scanner = null;

        try {
            scanner = new Scanner(tapFile);
            String line = null;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (StringUtils.isNotBlank(line)) {
                    this.parseLine(line);
                }
            }
            this.postProcess();
        } catch (Exception e) {
            throw new ParserException("Error parsing TAP Stream: " +
                                      e.getMessage(), e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return this.getTestSet();
    }

    /**
	 * 
	 */
    private void setIndentationLevelIfNotDefined(String tapLine) {
        if (this.isBaseIndentationAlreadyDefined() == Boolean.FALSE) {
            this.baseIndentationLevel = this.getIndentationLevel(tapLine);
        }
    }

    /**
     * Checks if the indentation is greater than the
     * {@link #baseIndentationLevel}
     * 
     * @param indentation indentation level
     * @throws org.tap4j.consumer.TapConsumerException if indentation is less
     *         then the {@link #baseIndentationLevel} .
     */
    private void checkIndentationLevel(int indentation, String tapLine) {
        if (indentation < this.baseIndentationLevel) {
            if (!this.currentlyInYAML &&
                this.mementos.isEmpty() == Boolean.FALSE) {
                while (!this.mementos.isEmpty() &&
                       indentation < this.baseIndentationLevel) {
                    this.loadMemento();
                }
            } else {
                throw new ParserException("Invalid indentantion. " +
                                          "Check your TAP Stream. Line: " +
                                          tapLine);
            }
        }
    }

    /**
     * Gets the indentation level of a line.
     * 
     * @param tapLine line.
     * @return indentation level of a line.
     */
    private int getIndentationLevel(String tapLine) {
        int indentationLevel = 0;

        final Matcher indentMatcher = INDENTANTION_PATTERN.matcher(tapLine);

        if (indentMatcher.matches()) {
            String spaces = indentMatcher.group(1);
            indentationLevel = spaces.length();
        }
        return indentationLevel;
    }

    /**
     * <p>
     * Checks if there is any diagnostic information on the diagnostic buffer.
     * </p>
     * <p>
     * If so, tries to parse it using snakeyaml.
     * </p>
     * 
     * @throws org.tap4j.consumer.TapConsumerException
     */
    private void checkAndParseTapDiagnostic() {
        // If we found any meta, then process it with SnakeYAML
        if (diagnosticBuffer.length() > 0) {

            if (this.lastParsedElement == null) {
                throw new ParserException(
                                          "Found diagnostic information without a previous TAP element.");
            }

            try {
                // Iterable<?> metaIterable = (Iterable<?>)yaml.loadAll(
                // diagnosticBuffer.toString() );
                @SuppressWarnings("unchecked")
                Map<String, Object> metaIterable = (Map<String, Object>) yaml
                    .load(diagnosticBuffer.toString());
                this.lastParsedElement.setDiagnostic(metaIterable);
            } catch (Exception ex) {
                throw new ParserException("Error parsing YAML [" +
                                          diagnosticBuffer.toString() + "]: " +
                                          ex.getMessage(), ex);
            }

            diagnosticBuffer = new StringBuilder();
        }
    }

    /*
     * Checks if the Header was set.
     * @throws org.tap4j.consumer.TapConsumerException
     * @deprecated
     */
    // void checkHeader()
    // throws TapConsumerException
    // {
    // if ( this.header == null )
    // {
    // throw new TapConsumerException("Missing required TAP Header element.");
    // }
    // }

    /**
     * Appends a diagnostic line to diagnostic buffer. If the diagnostic line
     * contains --- or ... then it ignores this line. In the end of each line it
     * appends a break line.
     * 
     * @param diagnosticLine diagnostic line
     */
    private void appendTapLineToDiagnosticBuffer(String diagnosticLine) {
        if (diagnosticLine.trim().equals("---") ||
            diagnosticLine.trim().equals("...")) {
            return;
        }

        if (this.currentlyInYAML) {
            diagnosticBuffer.append(diagnosticLine);
            diagnosticBuffer.append('\n');
        }
    }

    /**
     * @return true if the base indentation is already defined, false otherwise.
     */
    protected boolean isBaseIndentationAlreadyDefined() {
        return this.baseIndentationLevel >= 0;
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.consumer.DefaultTapConsumer#postProcess()
     */
    protected void postProcess() {
        this.checkTAPPlanIsSet();
        this.checkAndParseTapDiagnostic();
    }

}
