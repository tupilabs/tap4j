/*
 * The MIT License Copyright (c) <2010> <tap4j> Permission is hereby granted,
 * free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.tap4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerImpl;
import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducer;
import org.tap4j.representer.Tap13Representer;
import org.tap4j.util.StatusValues;

/**
 * Test where the producer outputs a tap file and then a consumer reads it and
 * checks if the values are correct. For example, you create a test with a Test
 * Result with a String there. Then you use the consumer to read the tap file
 * created and check if the consumer can read the String. Voila.
 * 
 * @since 1.0
 */
public class TestProduceConsume {

    private static final Integer TAP_VERSION = 13;
    private Producer tapProducer;
    private TapConsumer tapConsumer;
    private TestSet testSet;
    // Temp file to where we output the generated tap stream.
    private File tempFile;

    private static final Integer INITIAL_TEST_STEP = 1;

    @Before
    public void setUp() {
        tapProducer = new TapProducer(new Tap13Representer());
        tapConsumer = new TapConsumerImpl();

        testSet = new TestSet();

        Header header = new Header(TAP_VERSION);
        testSet.setHeader(header);

        Plan plan = new Plan(INITIAL_TEST_STEP, 3);
        Comment commentPlan = new Comment(
                                          "Testing something with a plan that I do not know exactly what it is about!");
        plan.setComment(commentPlan);
        testSet.setPlan(plan);

        Comment singleComment = new Comment("Starting tests");
        testSet.addComment(singleComment);

        TestResult tr1 = new TestResult(StatusValues.OK, 1);
        testSet.addTestResult(tr1);

        TestResult tr2 = new TestResult();
        tr2.setStatus(StatusValues.NOT_OK);
        tr2.setTestNumber(testSet.getNextTestNumber());
        testSet.addTestResult(tr2);

        TestResult tr3 = new TestResult(StatusValues.OK, 3);
        Comment commentTr3 = new Comment("Test 3 :)");
        tr3.addComment(commentTr3);
        testSet.addTestResult(tr3);

        testSet.setFooter(new Footer("End"));

        try {
            tempFile = File.createTempFile("tap4j_", ".tap");
        } catch (IOException e) {
            fail("Failed to create temp file: " + e.getMessage());
        }
    }

    @Test
    public void testTapProducerConsumer() {
        assertTrue(testSet.getTapLines().size() > 0);
        
        // testProducer
        try {
            tapProducer.dump(testSet, tempFile);
        } catch (Exception e) {
            fail("Failed to print TAP Stream into file.");
        }
        
        // testConsumer
        { 
            TestSet testSet = tapConsumer.load(tempFile);
            assertNotNull(testSet.getHeader());
            assertNotNull(testSet.getPlan());
            assertTrue(testSet.getNumberOfTestResults() == 3);
            assertNotNull(testSet.getFooter());
            assertTrue(testSet.getTapLines().size() > 0);
            assertTrue(testSet.getNumberOfTapLines() > 0);
            assertTrue(testSet.containsOk());
            assertFalse(testSet.containsBailOut());
            assertTrue(testSet.containsNotOk());
            assertTrue(testSet.getComments().size() > 0);
            assertTrue(testSet.getNumberOfComments() > 0);
            assertTrue(testSet.getComments().size() == testSet
                .getNumberOfComments());
            assertNotNull(tapConsumer.getTestSet());
            assertEquals(testSet.getTestResult(1).getStatus(),
                                StatusValues.OK);
        }
    }


    @Test
    public void testWithBailOut() {
        final BailOut bailOut = new BailOut(null);
        tapConsumer.getTestSet().getBailOuts().add(bailOut);
        assertTrue(tapConsumer.getTestSet().containsBailOut());
    }
}
