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
package org.tap4j.parser.issue3409478;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;

/**
 * <p>Test class for issue 3409478.</p>
 * <p>
 * Perl's Test::More supports a 'done_testing()', so you do not need to
 * predeclare how many tests you are running, this produces:
 * </p>
 * <ul>
 * <li>ok 1 - Test1;</li>
 * <li>ok 2 - got document root</li>
 * <li>ok 3 - document root ok</li>
 * <li>1..3</li>
 * <li>ok</li>
 * </ul>
 * 
 * @since 2.0.6
 */
public class TestDoneTesting {

    TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();

    @Test
    public void testDoneTesting() {
        String tapStream = "ok 1 - Test1;\n" + "ok 2 - got document root\n"
                           + "ok 3 - document root ok\n" + "1..3\n" + "ok";

        TestSet testSet = consumer.load(tapStream);
        assertEquals(3, testSet.getTestResults().size());
    }

}
