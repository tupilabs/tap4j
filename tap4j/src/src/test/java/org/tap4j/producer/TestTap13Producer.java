/*
 * The MIT License Copyright (c) 2010 Bruno P. Kinoshita
 * <http://www.kinoshita.eti.br> Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software. THE SOFTWARE
 * IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tap4j.producer;

import java.io.File;
import java.io.IOException;

import org.tap4j.model.Comment;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTap13Producer {

    private static final Integer TAP_VERSION = 13;

    private Producer tapProducer;

    private TestSet testSet;

    // Temp file to where we output the generated tap stream.
    private File tempFile;

    private static final Integer INITIAL_TEST_STEP = 1;

    @BeforeTest
    public void setUp() {
        tapProducer = new TapProducer();
        testSet = new TestSet();
        Header header = new Header(TAP_VERSION);
        testSet.setHeader(header);
        Plan plan = new Plan(INITIAL_TEST_STEP, 3);
        testSet.setPlan(plan);
        Comment singleComment = new Comment("Starting tests");
        testSet.addComment(singleComment);

        TestResult tr1 = new TestResult(StatusValues.OK, 1);
        testSet.addTestResult(tr1);

        TestResult tr2 = new TestResult(StatusValues.NOT_OK, 2);
        tr2.setTestNumber(2);
        testSet.addTestResult(tr2);

        testSet.setFooter(new Footer("End"));

        try {
            tempFile = File.createTempFile("tap4j_", ".tap");
        } catch (IOException e) {
            Assert.fail("Failed to create temp file: " + e.getMessage(), e);
        }
    }

    @Test
    public void testTapProducer() {
        Assert.assertTrue(testSet.getTapLines().size() > 0);

        try {
            tapProducer.dump(testSet, tempFile);
        } catch (Exception e) {
            Assert.fail("Failed to print TAP Stream into file.", e);
        }

        // BufferedReader reader = null;
        //
        // try
        // {
        // reader = new BufferedReader( new FileReader( tempFile ) );
        //
        // String line = null;
        //
        // while ( (line = reader.readLine()) != null )
        // {
        // System.out.println(line);
        // }
        // }
        // catch (Exception e)
        // {
        // fail("Failed to read temp file.", e);
        // }
        // finally
        // {
        // if ( reader != null )
        // {
        // try
        // {
        // reader.close();
        // } catch (Exception e2)
        // {
        // e2.printStackTrace(System.err);
        // }
        // reader = null;
        // }
        // }
    }

    @Test
    public void testSingleTestResultWithoutTestNumber() {
        Producer tapProducer = new TapProducer();
        TestSet testSet = new TestSet();
        Plan plan = new Plan(1, 1);
        testSet.setPlan(plan);
        TestResult okTestResult = new TestResult();
        okTestResult.setStatus(StatusValues.OK);
        testSet.addTestResult(okTestResult);
        String output = tapProducer.dump(testSet);
        Assert.assertFalse(output.contains("-1"));

    }

}
