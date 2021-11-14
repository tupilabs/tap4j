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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;

/**
 * Tests for TAP JUnit representer.
 *
 * @since 3.1
 */
public class TestTapJunitRepresenter {

    private TestSet testSet;

    @Before
    public void setUp() {
        testSet = new TestSet();
        testSet.setPlan(new Plan(2));
        testSet.addTestResult(new TestResult(StatusValues.OK, 1));
        testSet.addTestResult(new TestResult(StatusValues.OK, 2));
    }

    @Test
    public void testOnlySuccess() {
        Representer r = new TapJunitRepresenter("OnlySuccess");
        String s = r.representData(testSet);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                          + "<testsuite failures=\"0\" time=\"0.0\" errors=\"0\" skipped=\"0\" tests=\"2\" name=\"OnlySuccess\">\n"
                          + "<testcase time=\"0\" classname=\"OnlySuccess\" name=\"null\">\n"
                          + "</testcase>\n"
                          + "<testcase time=\"0\" classname=\"OnlySuccess\" name=\"null\">\n"
                          + "</testcase>\n" + "</testsuite>";

        assertTrue("Wrong XML output", s.contains(expected));
    }

    @Test
    public void testWithFailures() {
        TestSet testSet2 = new TestSet();
        testSet2.setPlan(new Plan(3));
        testSet2.addTestResult(testSet.getTestResult(1));
        testSet2.addTestResult(testSet.getTestResult(2));
        testSet2.addTestResult(new TestResult(StatusValues.NOT_OK, 3));

        Representer r = new TapJunitRepresenter("WithFailures");
        String s = r.representData(testSet2);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                          + "<testsuite failures=\"1\" time=\"0.0\" errors=\"0\" skipped=\"0\" tests=\"3\" name=\"WithFailures\">\n"
                          + "<testcase time=\"0\" classname=\"WithFailures\" name=\"null\">\n"
                          + "</testcase>\n"
                          + "<testcase time=\"0\" classname=\"WithFailures\" name=\"null\">\n"
                          + "</testcase>\n"
                          + "<testcase time=\"0\" classname=\"WithFailures\" name=\"null\">\n"
                          + "<failure message=\"null\" type=\"Failure\" />\n"
                          + "</testcase>\n" + "</testsuite>";

        assertTrue("Wrong XML output", s.contains(expected));
    }

}
