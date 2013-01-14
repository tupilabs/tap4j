/*
 * The MIT License Copyright (c) <2012> <Bruno P. Kinoshita> Permission is
 * hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.tap4j.consumer.issue3504508;

import java.io.File;

import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestIssue3504508 {

    @Test
    public void testTapConsumer() {
        TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = consumer.load(new File(TestIssue3504508.class
            .getResource("/org/tap4j/consumer/issue3504508/sample.tap").getFile()));
        Assert.assertTrue(testSet.getTestResult(1).getSubtest() != null);

        Assert.assertTrue(testSet.getTestResult(1).getSubtest()
            .getTestResult(2).getSubtest() != null);

        Assert.assertTrue(testSet.getTestResults().size() == 3);
    }

    @Test
    public void testProducingSubtests() {
        final String expected = "1..3\n" + "ok 1 - First test\n" + "    1..2\n"
                                + "    ok 1 - This is a subtest\n"
                                + "    ok 2 - So is this\n" + "        1..2\n"
                                + "        ok 1 - This is a subtest\n"
                                + "        ok 2 - So is this\n"
                                + "ok 2 - An example subtest\n"
                                + "ok 3 - Third test\n";
        final TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();
        final TestSet testSet = consumer.load(new File(TestIssue3504508.class
            .getResource("/org/tap4j/consumer/issue3504508/sample.tap").getFile()));

        final Producer producer = new TapProducer();
        Assert.assertEquals(producer.dump(testSet), expected);
    }

}
