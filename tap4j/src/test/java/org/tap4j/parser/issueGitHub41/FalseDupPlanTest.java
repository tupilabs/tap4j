/*
 * The MIT License
 *
 * Copyright (c) 2016 tap4j team (see AUTHORS)
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
package org.tap4j.parser.issueGitHub41;

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
 * TAP Streams with a 1..0 plan could fail, complaining about a duplicate
 * TAP plan found.
 *
 * @since 4.2.1
 */
public class FalseDupPlanTest {

    /**
     * 1..0 plan should not break parser.
     */
    @Test
    public void testParsingWhenEmptyPlansPresent() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();

        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
                .getResource("/org/tap4j/parser/issueFalseDupPlan/npm-test.tap")
                .getFile()));

        assertNotNull(testSet);

        final List<TestResult> testResults = testSet.getTestResults();
        assertEquals(41, testResults.size());
    }
}
