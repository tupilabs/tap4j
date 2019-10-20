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
package org.tap4j.consumer.subtestOrder;

import org.junit.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;
import org.tap4j.producer.TapProducerFactory;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Tests for correct subtests order.
 *
 * @since 4.3
 */
public class TestSubtestOrder extends BaseTapTest {
    @Test
    public void testProducingSubtests() {
        final TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();
        final TestSet testSet = consumer.load(new File(TestSubtestOrder.class
                .getResource("/org/tap4j/consumer/subtestOrder/subtest.tap").getFile()));
        String expected = "1..2\n"
                + "ok 1 - First test\n"
                + "    1..1\n"
                + "        1..1\n"
                + "        ok 1 - Internal subtest subtest\n"
                + "    ok 1 - Internal subtest\n"
                + "ok 2 - Some subtest\n";
        assertEquals(expected, TapProducerFactory.makeTap13Producer().dump(testSet));
        assertNotNull(testSet.getTestResult(2).getSubtest());
        assertNotNull(testSet.getTestResult(2).getSubtest()
                .getTestResult(1).getSubtest());
    }

}
