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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;
import org.tap4j.util.TapVersionValues;

/**
 * @since 1.0
 */
public class TestTap13YamlConsumer extends BaseTapTest {

    @Test
    public void test1() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/1.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getHeader().getVersion());
        assertEquals(testSet.getHeader().getVersion(), TapVersionValues.TAP_13.getValue());
        assertNotNull(testSet.getPlan());
        assertEquals(1, (int) testSet.getPlan().getInitialTestNumber());
        assertEquals(3, (int) testSet.getPlan().getLastTestNumber());
        assertEquals(3, testSet.getTestResults().size());
        assertSame(testSet.getTestResult(1).getStatus(), StatusValues.OK);
        assertNotNull(testSet.getTestResult(2).getDiagnostic());
        assertSame(testSet.getTestResult(2).getStatus(), StatusValues.NOT_OK);
        final Map<String, Object> diagnostic = testSet.getTestResult(2)
                .getDiagnostic();
        assertNotNull(diagnostic);
        assertEquals(diagnostic.get("file"), "t/something.t");
        assertSame(testSet.getTestResult(3).getStatus(), StatusValues.OK);
    }

    @Test
    public void test2() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/org.tap4j.testng.TestGoogleBrunoKinoshita.tap");
        assertNotNull(testSet.getHeader());
        assertNotNull(testSet.getHeader().getVersion());
        assertEquals(testSet.getHeader().getVersion(), TapVersionValues.TAP_13.getValue());
        assertNotNull(testSet.getPlan());
        assertEquals(1, (int) testSet.getPlan().getInitialTestNumber());
        assertEquals(1, (int) testSet.getPlan().getLastTestNumber());
        assertEquals(1, testSet.getTestResults().size());
        assertSame(testSet.getTestResult(1).getStatus(), StatusValues.NOT_OK);
        assertNotNull(testSet.getTestResult(1).getDiagnostic());
        Map<String, Object> diagnostic = testSet.getTestResult(1)
                .getDiagnostic();
        assertNotNull(diagnostic);
        assertEquals(diagnostic.get("file"),
                "org.tap4j.testng.TestGoogleBrunoKinoshita.java");
    }

    @Test
    public void test3() {
        TestSet testSet = getTestSet("/org/tap4j/consumer/org.tap4j.testng.konobi.tap");
        assertNotNull(testSet.getPlan());
        assertEquals(1, (int) testSet.getPlan().getInitialTestNumber());
        assertEquals(1, (int) testSet.getPlan().getLastTestNumber());
        assertEquals(1, testSet.getTestResults().size());
        assertSame(testSet.getTestResult(1).getStatus(), StatusValues.NOT_OK);
        assertNotNull(testSet.getTestResult(1).getDiagnostic());
        Map<String, Object> diagnostic = testSet.getTestResult(1)
                .getDiagnostic();
        assertNotNull(diagnostic);
        assertEquals("org.tap4j.testng.konobi.java", diagnostic.get("file"));
        assertNotNull(diagnostic.get("stack"));
    }

}
