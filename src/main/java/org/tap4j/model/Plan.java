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
package org.tap4j.model;

/**
 * TAP Plan. The TAP Plan gives details about the execution of the tests such as
 * initial test number, last test number, flag to skip all tests and a reason
 * for this.
 *
 * @since 1.0
 */
public class Plan extends TapElement {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8517740981464132024L;

    /**
     * Default initial test step.
     */
    private static final Integer INITIAL_TEST_STEP = 1;

    /**
     * TAP Plan initial test number.
     */
    private Integer initialTestNumber;

    /**
     * TAP Plan last test number.
     */

    private Integer lastTestNumber;

    /**
     * TAP Plan skip. If present the tests should not be executed.
     */
    private SkipPlan skip;

    /**
     * Child subtest.
     */
    private TestSet subtest;

    /**
     * Constructor with parameters.
     *
     * @param initialTestNumber Initial test number (usually is 1).
     * @param lastTestNumber Last test number (may be 0 if to skip all tests).
     */
    public Plan(Integer initialTestNumber, Integer lastTestNumber) {
        super();
        this.initialTestNumber = initialTestNumber;
        this.lastTestNumber = lastTestNumber;
        this.subtest = null;
    }

    /**
     * Constructor with parameters.
     *
     * @param amountOfTests How many tests we have in the plan.
     */
    public Plan(Integer amountOfTests) {
        super();
        this.initialTestNumber = INITIAL_TEST_STEP;
        this.lastTestNumber = amountOfTests;
    }

    /**
     * Constructor with parameters.
     *
     * @param amountOfTests How many tests we have in the plan.
     * @param skip Plan skip with a reason.
     */
    public Plan(Integer amountOfTests, SkipPlan skip) {
        super();
        this.initialTestNumber = INITIAL_TEST_STEP;
        this.lastTestNumber = amountOfTests;
        this.skip = skip;
    }

    /**
     * Constructor with parameters.
     *
     * @param initialTestNumber Initial test number (usually is 1).
     * @param lastTestNumber Last test number (may be 0 if to skip all tests).
     * @param skip Plan skip with a reason.
     */
    public Plan(Integer initialTestNumber, Integer lastTestNumber, SkipPlan skip) {
        super();
        this.initialTestNumber = initialTestNumber;
        this.lastTestNumber = lastTestNumber;
        this.skip = skip;
    }

    /**
     * @return Initial test number.
     */
    public Integer getInitialTestNumber() {
        return this.initialTestNumber;
    }

    /**
     * @return Last test number.
     */
    public Integer getLastTestNumber() {
        return this.lastTestNumber;
    }

    /**
     * @return Flag used to indicate whether skip all tests or not.
     */
    public Boolean isSkip() {
        return this.skip != null;
    }

    /**
     * @return Plan Skip with reason.
     */
    public SkipPlan getSkip() {
        return this.skip;
    }

    /**
     * Defines whether we should skip all tests or not.
     *
     * @param skip Plan Skip.
     */
    public void setSkip(SkipPlan skip) {
        this.skip = skip;
    }

    /**
     * @return the subtest
     */
    public TestSet getSubtest() {
        return subtest;
    }

    /**
     * @param subtest the subtest to set
     */
    public void setSubtest(TestSet subtest) {
        this.subtest = subtest;
    }
}
