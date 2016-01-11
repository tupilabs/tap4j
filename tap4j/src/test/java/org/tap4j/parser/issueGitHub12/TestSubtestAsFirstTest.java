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
package org.tap4j.parser.issueGitHub12;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

/**
 * TAP Streams with a subtest as first test fail, complaining about a duplicate
 * TAP plan found.
 * 
 * @since 4.0.2
 */
public class TestSubtestAsFirstTest {

    /**
     * Tests substests as first test in a TAP Stream.
     */
    @Test
    public void testSubtestAsFirstTest() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
            .getResource("/org/tap4j/parser/issueGitHub12/issue-12-tap-stream.tap")
            .getFile()));

        assertNotNull(testSet);
        
        final List<TestResult> testResults = testSet.getTestResults();
        
        assertEquals(2, testResults.size());
        
        assertEquals(1, testResults.get(0).getSubtest().getTestResults().size());
        assertEquals(4, testResults.get(1).getSubtest().getTestResults().size());
    }
}
