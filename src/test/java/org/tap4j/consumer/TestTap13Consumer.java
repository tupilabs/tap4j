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
package org.tap4j.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.model.Footer;
import org.tap4j.model.SkipPlan;
import org.tap4j.model.TestSet;
import org.tap4j.parser.ParserException;

/**
 * Tests for a TAP 13 parser. Without YAML or subtests.
 * @since 1.0
 */
public class TestTap13Consumer extends BaseTapTest {// NOPMD

    // comment_planskipall.tap
    @Test
    public void testConsumerPlanskipall() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/comment_planskipall.tap");
        assertEquals(0, testSet.getNumberOfTestResults());

        final Footer footer = testSet.getFooter();
        assertNull(footer);

        final SkipPlan skip = testSet.getPlan().getSkip();
        assertNotNull(skip);
        assertEquals("Not implemented yet.", skip.getReason());
    }

    // header_plan_tr_footer.tap
    @Test
    public void testConsumerHeaderPlanTrFooter() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/header_plan_tr_footer.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getPlan());
        assertEquals(2, testSet.getNumberOfTestResults());
        assertEquals("Test 1", testSet.getTestResults().get(0).getDescription());
        assertNotNull(testSet.getFooter());
        assertNotNull(testSet.getFooter().getComment());
    }

    // header_plan_tr.tap
    @Test
    public void testConsumerHeaderPlanTr() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/header_plan_tr.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getPlan());
        assertEquals(2, testSet.getNumberOfTestResults());
        assertEquals("Test 1", testSet.getTestResults().get(0).getDescription());
        assertNull(testSet.getFooter());
    }

    // header_plan.tap
    @Test
    public void testConsumerHeaderPlan() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/header_plan.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getPlan());
        assertEquals(0, testSet.getNumberOfTestResults());
        assertNull(testSet.getFooter());
    }

    // header_planskipall.tap
    @Test
    public void testConsumerHeaderPlanskipall() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/header_planskipall.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getPlan());
        assertTrue(testSet.getPlan().isSkip());
        assertNotNull(testSet.getPlan().getSkip());
        assertEquals(0, testSet.getNumberOfTestResults());
        assertNull(testSet.getFooter());
    }

    // header_tr_plan.tap
    @Test
    public void testConsumerHeaderTrPlan() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/header_tr_plan.tap");
        assertEquals(2, testSet.getTestResults().size());
        assertNotNull(testSet.getPlan());
        // assertFalse(
        // ((Tap13YamlParser)consumer).isPlanBeforeTestResult() );
    }

    // plan_comment_tr_footer.tap
    @Test
    public void testConsumerPlanCommentTrFooter() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/plan_comment_tr_footer.tap");
        assertNull(testSet.getHeader());
        assertEquals(3, testSet.getTestResults().size());
        assertNotNull(testSet.getPlan());
        // assertTrue(
        // ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
        assertEquals(testSet.getTestResults().size(), (int) testSet.getPlan()
                .getLastTestNumber());
        assertNotNull(testSet.getFooter());
    }

    // plan_tr.tap
    @Test
    public void testConsumerPlanTr() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/plan_tr.tap");
        assertNull(testSet.getHeader());
        assertEquals(2, testSet.getTestResults().size());
        assertNotNull(testSet.getPlan());
        // assertTrue(
        // ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
        assertEquals(testSet.getTestResults().size(), (int) testSet.getPlan()
                .getLastTestNumber());
        assertNull(testSet.getFooter());
    }

    // invalid_comment_tr_bailout_header.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidCommentTrBailoutHeader() {
        getTestSet("/org/tap4j/consumer/invalid_comment_tr_bailout_header.tap");
        fail("Not supposed to get here");
    }

    // invalid_header_tr.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidHeaderTr() {
        getTestSet("/org/tap4j/consumer/invalid_header_tr.tap");
        fail("Not supposed to get here");
    }

    // invalid_plan_header_plan.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidPlanHeaderPlan() {
        getConsumer().load("/org/tap4j/consumer/invalid_plan_header_plan.tap");
        fail("Not supposed to get here");
    }

    // invalid_plan_tr_header.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidPlanTrHeader() {
        getTestSet("/org/tap4j/consumer/invalid_plan_tr_header.tap");
        fail("Not supposed to get here");
    }

    // invalid_tr_footer.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidTrFooter() {
        getTestSet("/org/tap4j/consumer/invalid_tr_footer.tap");
        fail("Not supposed to get here");
    }

    // invalid_tr_header_header_tr.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidTrHeaderHeaderTr() {
        getTestSet("/org/tap4j/consumer/invalid_tr_header_header_tr.tap");
        fail("Not supposed to get here");
    }

    // invalid_tr_plan_header.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidTrPlanHeader() {
        getTestSet("/org/tap4j/consumer/invalid_tr_plan_header.tap");
        fail("Not supposed to get here");
    }

    // invalid_tr.tap
    @Test(expected = TapConsumerException.class)
    public void testConsumerInvalidTr() {
        getTestSet("/org/tap4j/consumer/invalid_tr.tap");
        fail("Not supposed to get here");
    }

    @Test
    public void testConsumerTapStream1AndPrintDetails() {
        String tapStream = "TAP version 13 # the header\n" +
                "1..1\n" +
                "ok 1\n" +
                "Bail out! Out of memory exception # Contact admin! 9988\n";
        final TestSet testSet = getConsumer().load(tapStream);
        assertEquals(1, (int) testSet.getPlan().getLastTestNumber());
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getHeader().getComment());
        assertEquals(testSet.getBailOuts().get(0).getReason(),
                            "Out of memory exception ");
        assertEquals(testSet.getBailOuts().get(0).getComment()
            .getText(), "Contact admin! 9988");
    }

    /**
     * Tests a TapConsumer reading an invalid TAP Stream.
     */
    @Test
    public void testTapConsumerInvalidToken() {
        final TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = null;
        String invalidTapStream = "a..2\nok 1\n  ---\n  name:Bruno\n  ---ok2";
        try {
            testSet = tapConsumer.load(invalidTapStream);
            fail("Not supposed to get here");
        } catch (TapConsumerException tapConsumerException) {
            assertTrue(tapConsumerException.getCause() instanceof ParserException);
        }
        assertNull(testSet);
    }

    /**
     * Tests a TapConsumer with a single Test Result.
     */
    @Test
    public void testWithSingleTestResult() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/single_tr.tap");
        assertNotNull(testSet);
        assertEquals(1, testSet.getNumberOfTestResults());
    }

    /**
     * A Tap Consumer cannot save its state.
     */
    @Test
    public void testStateOfConsumer() {
        TestSet testSet = getTestSet("/org/tap4j/consumer/single_tr.tap");
        assertNotNull(testSet);
        assertEquals(1, testSet.getNumberOfTestResults());
        testSet = getTestSet("/org/tap4j/consumer/two_tr.tap");
        assertNotNull(testSet);
        assertEquals(2, testSet.getNumberOfTestResults());
    }

}
