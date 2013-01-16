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

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
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

/**
 * TAP 13 parser.
 * 
 * @since 1.0
 */
public class Tap13Parser implements Parser {

    private boolean isFirstLine = true;

    private boolean planBeforeTestResult = false;

    // Helper String to check the Footer
    private String lastLine = null;

    /**
     * Test Set.
     */
    private TestSet testSet;

    /**
     * Default constructor. Calls the init method.
     */
    public Tap13Parser() {
        super();
        this.init();
    }

    /**
     * Called from the constructor and everytime a new TAP Stream (file or
     * string) is processed.
     */
    public final void init() {
        this.isFirstLine = true;
        this.planBeforeTestResult = false;
        this.lastLine = null;
        this.testSet = new TestSet();
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
     * @see org.tap4j.TapConsumer#parseLine(java.lang.String)
     */
    public void parseLine(String tapLine) {
        if (StringUtils.isEmpty(tapLine)) {
            return;
        }

        Matcher matcher = null;

        // Comment
        matcher = COMMENT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractComment(matcher);
            return;
        }

        // Last line that is not a comment.
        lastLine = tapLine;

        // Header
        matcher = HEADER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {

            this.checkTAPHeaderParsingLocationAndDuplicity();

            this.extractHeader(matcher);
            this.isFirstLine = false;
            return;
        }

        // Plan
        matcher = PLAN_PATTERN.matcher(tapLine);
        if (matcher.matches()) {

            this.checkTAPPlanDuplicity();

            this.checkIfTAPPlanIsSetBeforeTestResultsOrBailOut();

            this.extractPlan(matcher);
            this.isFirstLine = false;
            return;
        }

        // Test Result
        matcher = TEST_RESULT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractTestResult(matcher);
            return;
        }

        // Bail Out
        matcher = BAIL_OUT_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractBailOut(matcher);
            return;
        }

        // Footer
        matcher = FOOTER_PATTERN.matcher(tapLine);
        if (matcher.matches()) {
            this.extractFooter(matcher);
            return;
        }

        // Any text. It should not be parsed by the consumer.
        final Text text = new Text(tapLine);
        this.testSet.getTapLines().add(text);
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
        if (!isFirstLine) {
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
        this.testSet.addTapLine(testResult);
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
        this.testSet.addTapLine(bailOut);
    }

    /**
     * @param matcher REGEX Matcher.
     */
    protected void extractComment(Matcher matcher) {
        String text = matcher.group(1);
        Comment comment = new Comment(text);

        this.testSet.addComment(comment);
        this.testSet.addTapLine(comment);
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
     * @throws org.tap4j.consumer.TapConsumerException
     */
    protected void postProcess() {
        // deprecated for better interoperability with Perl done_testing()
        // this.checkTAPPlanPosition();
        this.checkTAPPlanIsSet();
    }

}
