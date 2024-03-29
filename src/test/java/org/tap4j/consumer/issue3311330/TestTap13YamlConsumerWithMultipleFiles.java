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
package org.tap4j.consumer.issue3311330;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.model.TestSet;

/**
 * @since 2.0.4
 */
public class TestTap13YamlConsumerWithMultipleFiles extends BaseTapTest {

    @Test
    public void testConsumerWithMultipleFiles() {
        TestSet testSet = getTestSet("/org/tap4j/consumer/issue3311330/1.tap");
        assertNotNull("Empty Test Set", testSet);
        assertEquals("Wrong number of tests", 2, testSet.getTestResults().size());

        testSet = getTestSet("/org/tap4j/consumer/issue3311330/fala.tap");
        assertNotNull("Empty Test Set", testSet);
        assertEquals("Wrong number of tests", 3, testSet.getTestResults().size());

        testSet = getTestSet("/org/tap4j/consumer/issue3311330/oi.tap");
        assertNotNull("Empty Test Set", testSet);
        assertEquals("Wrong number of tests", 1, testSet.getTestResults().size());
    }

}
