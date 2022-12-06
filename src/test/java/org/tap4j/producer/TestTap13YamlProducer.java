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
package org.tap4j.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.representer.DumperOptions;
import org.tap4j.representer.Representer;
import org.tap4j.representer.Tap13Representer;
import org.tap4j.util.StatusValues;

/**
 * @since 1.0
 */
public class TestTap13YamlProducer {

    private static final Integer TAP_VERSION = 13;
    private Producer tapProducer;
    private TestSet testSet;
    // Temp file to where we output the generated tap stream.
    private File tempFile;

    private static final Integer INITIAL_TEST_STEP = 1;

    @BeforeEach
    public void setUp() {
        DumperOptions options = new DumperOptions();
        options.setAllowEmptyTestPlan(Boolean.FALSE);
        tapProducer = new TapProducer(new Tap13Representer(options));
        testSet = new TestSet();
        Header header = new Header(TAP_VERSION);
        testSet.setHeader(header);
        Plan plan = new Plan(INITIAL_TEST_STEP, 3);
        testSet.setPlan(plan);
        Comment singleComment = new Comment("Starting tests");
        testSet.addComment(singleComment);

        TestResult tr1 = new TestResult(StatusValues.OK, 1);
        LinkedHashMap<String, Object> diagnostic = new LinkedHashMap<>();
        diagnostic.put("file", "testingproducer.txt");
        diagnostic.put("time", System.currentTimeMillis());
        diagnostic.put("Tester", "Bruno P. Kinoshita");
        LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
        map2.put("EHCTA", 1233);
        map2.put("TRANSACTION", 3434);
        diagnostic.put("Audit", map2);
        tr1.setDiagnostic(diagnostic);
        // tr1.setDiagnostic(diagnostic)
        testSet.addTestResult(tr1);

        TestResult tr2 = new TestResult(StatusValues.NOT_OK, 2);
        tr2.setTestNumber(2);
        testSet.addTestResult(tr2);

        BailOut bailOut = new BailOut("Test 2 failed");
        testSet.addBailOut(bailOut);

        Comment simpleComment = new Comment("Test bailed out.");
        testSet.addComment(simpleComment);

        testSet.setFooter(new Footer("End"));

        try {
            tempFile = File.createTempFile("tap4j_", ".tap");
        } catch (IOException e) {
            fail("Failed to create temp file: " + e.getMessage());
        }
    }

    @Test
    public void testTapProducer() {
        assertTrue(testSet.getTapLines().size() > 0);

        assertEquals(2, testSet.getNumberOfTestResults());

        assertEquals(1, testSet.getNumberOfBailOuts());

        assertEquals(2, testSet.getNumberOfComments());

        try {
            tapProducer.dump(testSet, tempFile);

            // System.out.println(tempFile);
        } catch (Exception e) {
            fail("Failed to print TAP Stream into file.");
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
    public void testDumpFailsForMissingPlan() {
        DumperOptions options = new DumperOptions();
        options.setAllowEmptyTestPlan(Boolean.FALSE);
        Representer representer = new Tap13Representer(options);
        Producer tapProducer = new TapProducer(representer);

        TestSet testSet = new TestSet();
        TestResult okTestResult = new TestResult(StatusValues.OK,
                1);
        assertTrue(testSet.addTestResult(okTestResult));

        try {
            tapProducer.dump(testSet, (File) null);
        } catch (NullPointerException npe) {
            assertNotNull(npe);
        }

        StringWriter sw = new StringWriter();

        Assertions.assertThrows(ProducerException.class, () -> tapProducer.dump(testSet, sw));
    }

    @Test
    public void testDumpToStringWriter() {
        Producer tapProducer = new TapProducer();

        TestSet testSet = new TestSet();
        TestResult okTestResult = new TestResult(StatusValues.OK,
                1);
        assertTrue(testSet.addTestResult(okTestResult));

        assertNull(testSet.getPlan());

        Plan plan = new Plan(1, 1);
        testSet.setPlan(plan);

        assertNotNull(testSet.getPlan());

        StringWriter sw = new StringWriter();

        tapProducer.dump(testSet, sw);
    }

    @Test
    public void testDumpToNullWriter() {
        Producer tapProducer = new TapProducer();

        TestSet testSet = new TestSet();
        TestResult okTestResult = new TestResult(StatusValues.OK,
                1);
        assertTrue(testSet.addTestResult(okTestResult));

        assertNull(testSet.getPlan());

        Plan plan = new Plan(1, 1);
        testSet.setPlan(plan);

        assertNotNull(testSet.getPlan());

        Assertions.assertThrows(NullPointerException.class, () -> tapProducer.dump(testSet, (StringWriter) null));
    }

    @Test
    public void testDumpToInvalidFile() {
        Producer tapProducer = new TapProducer();

        TestSet testSet = new TestSet();
        TestResult okTestResult = new TestResult(StatusValues.OK,
                1);
        assertTrue(testSet.addTestResult(okTestResult));

        assertNull(testSet.getPlan());

        Plan plan = new Plan(1, 1);
        testSet.setPlan(plan);

        assertNotNull(testSet.getPlan());

        File outputFile = new File("");

        Assertions.assertThrows(ProducerException.class, () -> tapProducer.dump(testSet, outputFile));
    }

    @Test
    public void testDumpToInvalidWriter() {
        final Producer tapProducer = new TapProducer();
        final TestSet testSet = new TestSet();
        final TestResult okTestResult = new TestResult(StatusValues.OK,
                1);
        assertTrue(testSet.addTestResult(okTestResult));
        assertNull(testSet.getPlan());
        final Plan plan = new Plan(1, 1);
        testSet.setPlan(plan);
        assertNotNull(testSet.getPlan());
        File tempFile = null;
        try {
            tempFile = File.createTempFile("delete_", ".delete");
        } catch (IOException e) {
            fail("Failed to create temp file: " + e.getMessage());
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFile);
            writer.close();
        } catch (IOException e) {
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    tempFile.deleteOnExit();
                }
            }

            fail("Failed to create writer: " + e.getMessage());
        }

        try {
            FileWriter finalWriter = writer;
            Assertions.assertThrows(ProducerException.class, () -> tapProducer.dump(testSet, finalWriter));
        } finally {
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    tempFile.deleteOnExit();
                }
            }
        }
    }

}
