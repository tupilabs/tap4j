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
package org.tap4j.representer;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.tap4j.model.BailOut;
import org.tap4j.model.TapElement;
import org.tap4j.model.TapResult;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;


/**
 * TAP JUnit representer. Outputs Junit XML.
 *
 * @since 3.1
 */
public class TapJunitRepresenter implements Representer {

    /**
     * Name of test suite.
     */
    private String name;

    /**
     * @param name Test suite name.
     */
    public TapJunitRepresenter(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String representData(TestSet testSet) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); // TBD: get
                                                                   // encoding
                                                                   // from
                                                                   // dumper
                                                                   // options
        Statuses statuses = this.getStatuses(testSet);
        pw.println("<testsuite failures=\"" + statuses.getFailures() + "\" "
                + "time=\"" + statuses.getTime() + "\" errors=\""
                + statuses.getErrors() + "\" " + "skipped=\""
                + statuses.getSkipped() + "\" tests=\"" + statuses.getTests()
                + "\" " + "name=\"" + this.name + "\">");
        // TBD: output TAP header, TAP plan as properties
        for (TapElement tapLine : testSet.getTapLines()) {
            if (tapLine instanceof TestResult) {
                pw.println("<testcase time=\"0\" classname=\"" + this.name
                        + "\" name=\""
                        + ((TestResult) tapLine).getDescription() + "\">");
                if (((TestResult) tapLine).getDirective() != null
                        && ((TestResult) tapLine).getDirective()
                                .getDirectiveValue() == DirectiveValues.SKIP) {
                    pw.println("<skipped/>");
                }
                if (((TestResult) tapLine).getStatus() == StatusValues.NOT_OK) {
                    pw.println("<failure message=\""
                            + ((TestResult) tapLine).getDescription()
                            + "\" type=\"Failure\" />");
                }
                pw.println("</testcase>");
            }
            if (tapLine instanceof BailOut) {
                pw.println("<testcase time=\"0\" classname=\"" + this.name
                        + "\" name=\"" + ((BailOut) tapLine).getReason()
                        + "\">");
                pw.println("<error message=\""
                        + ((BailOut) tapLine).getReason()
                        + "\" type=\"BailOut\"/>");
                pw.println("</testcase>");
            }
        }
        pw.println("</testsuite>");
        return sw.toString();
    }

    /**
     * @param testSet Test Set
     * @return Statuses
     */
    private Statuses getStatuses(TestSet testSet) {
        Statuses statuses = new Statuses();
        statuses.setErrors(testSet.getBailOuts().size());
        for (TestResult tr : testSet.getTestResults()) {
            statuses.setTests(statuses.getTests() + 1);
            if (tr.getDirective() != null
                    && tr.getDirective().getDirectiveValue() == DirectiveValues.SKIP) {
                statuses.setSkipped(statuses.getSkipped() + 1);
            } else if (tr.getStatus() == StatusValues.NOT_OK) {
                statuses.setFailures(statuses.getFailures() + 1);
            }
        }
        return statuses;
    }

}

/**
 * Helper class for Junit report.
 *
 * @since 3.1
 */
class Statuses {

    /**
     * Number of errors.
     */
    private int errors;

    /**
     * Number of failures.
     */
    private int failures;

    /**
     * Number of skipped tests.
     */
    private int skipped;

    /**
     * Number of tests.
     */
    private int tests;

    /**
     * Time.
     */
    private double time;

    /**
     * Default constructor.
     */
    public Statuses() {
        super();
    }

    /**
     * @return the errors
     */
    public int getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(int errors) {
        this.errors = errors;
    }

    /**
     * @return the failures
     */
    public int getFailures() {
        return failures;
    }

    /**
     * @param failures the failures to set
     */
    public void setFailures(int failures) {
        this.failures = failures;
    }

    /**
     * @return the skipped
     */
    public int getSkipped() {
        return skipped;
    }

    /**
     * @param skipped the skipped to set
     */
    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    /**
     * @return the tests
     */
    public int getTests() {
        return tests;
    }

    /**
     * @param tests the tests to set
     */
    public void setTests(int tests) {
        this.tests = tests;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }

}
