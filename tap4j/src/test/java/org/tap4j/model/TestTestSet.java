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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.tap4j.util.StatusValues;

/**
 * @since 1.0
 */
public class TestTestSet {

    protected TestSet testSet;
    protected Header header;
    protected Plan plan;
    protected Footer footer;
    protected BailOut bailOut;
    protected Comment comment;
    protected TestResult tr1;
    protected Text text;

    @Before
    public void setUp() {
        testSet = new TestSet();
        // Header
        header = new Header(13);
        testSet.setHeader(header);
        // Plan
        plan = new Plan(3);
        testSet.setPlan(plan);
        // Comment
        comment = new Comment("Starting tests...");
        testSet.addComment(comment);
        // Test Results
        tr1 = new TestResult(StatusValues.OK, 1);
        testSet.addTestResult(tr1);
        text = new Text("Ignore this line please.");
        testSet.addTapLine(text);
        // Bail Out
        bailOut = new BailOut("Uck!");
        testSet.addBailOut(bailOut);
        // Footer
        footer = new Footer("TestSet Footer");
        testSet.setFooter(footer);
    }

    @Test
    public void testTestSet() {
        assertNotNull(this.testSet);
        assertNotNull(this.testSet.getHeader());
        assertNotNull(this.testSet.getPlan());
        assertTrue(this.testSet.getComments().size() == 1);
        assertTrue(this.testSet.getNumberOfComments() == 1);
        assertTrue(this.testSet.getNumberOfTapLines() == this.testSet
            .getTapLines().size());
        assertTrue(this.testSet.getNumberOfTapLines() == 4);
        assertTrue(this.testSet.getNumberOfTestResults() == this.testSet
            .getTestResults().size());
        assertTrue(this.testSet.getNumberOfTestResults() == 1);
        assertTrue(this.testSet.getNextTestNumber() == 2);
        assertTrue(this.testSet.hasBailOut());
        assertTrue(this.testSet.getBailOuts().size() == 1);
        assertTrue(this.testSet.getNumberOfBailOuts() == 1);
        assertNotNull(this.testSet.getFooter());
        
        // intrusive tests
        {
            this.testSet.removeBailOut(bailOut);
            assertTrue(this.testSet.getNumberOfBailOuts() == 0);

            this.testSet.removeComment(comment);
            assertTrue(this.testSet.getNumberOfComments() == 0);
            assertFalse(this.testSet.removeComment(comment));

            this.testSet.removeTestResult(tr1);
            assertTrue(this.testSet.getTestResults().size() == 0);
            assertFalse(this.testSet.removeTestResult(tr1));

            this.testSet.removeTapLine(text);
            assertTrue(this.testSet.getTapLines().size() == 0);
        }
    }

    @Test
    public void testTestSetWithOnlyTestResults() {
        final TestSet testSet = new TestSet();
        assertFalse(testSet.containsOk());
        assertFalse(testSet.containsNotOk());
        assertFalse(testSet.containsBailOut());
        final TestResult okTestResult = new TestResult();
        okTestResult.setStatus(StatusValues.OK);
        assertTrue(testSet.addTestResult(okTestResult));
        assertTrue(testSet.containsOk());
        final TestResult notOkTestResult = new TestResult();
        notOkTestResult.setStatus(StatusValues.NOT_OK);
        assertTrue(testSet.addTestResult(notOkTestResult));
        assertTrue(testSet.containsNotOk());
    }

    @Test
    public void testTestSetWithATestResultWithNullTestNumber() {
        final TestSet testSet = new TestSet();
        assertFalse(testSet.containsOk());
        assertFalse(testSet.hasBailOut());
        assertFalse(testSet.removeBailOut(new BailOut("False bailout")));
        final TestResult okTestResult = new TestResult();
        okTestResult.setStatus(StatusValues.OK);
        okTestResult.setTestNumber(null);
        assertTrue(testSet.addTestResult(okTestResult));
        assertTrue(testSet.containsOk());
        TestResult testResult = testSet.getTestResult(Integer.valueOf(1));
        assertNotNull(testResult);
        okTestResult.setTestNumber(Integer.valueOf(1));
        assertTrue(testSet.addTestResult(okTestResult));
        testResult = testSet.getTestResult(Integer.valueOf(1));
        assertNotNull(testResult);
    }

    @Test
    public void testWithOnlyNotOkTestResults() {
        final TestSet testSet = new TestSet();
        final TestResult notOkTestResult = new TestResult();
        notOkTestResult.setStatus(StatusValues.NOT_OK);
        assertFalse(testSet.containsNotOk());
        assertFalse(testSet.containsOk());
        assertTrue(testSet.addTestResult(notOkTestResult));
        assertTrue(testSet.containsNotOk());
        assertFalse(testSet.containsOk());
    }
    
    @Test
    public void testEmptyTestSet() {
        final TestSet testSet = new TestSet();
        assertNull(testSet.getTestResult(1));
    }

}
