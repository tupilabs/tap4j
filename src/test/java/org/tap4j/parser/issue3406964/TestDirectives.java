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
package org.tap4j.parser.issue3406964;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;

/**
 * Test class for issue 3406964. The skip directive in TAP can contain both
 * upper and lower case SKIP and TO DO text. However tap4j is considering only
 * the upper case version.
 *
 * @since 2.0.5
 */
public class TestDirectives {

    private TapConsumer consumer;

    @Test
    public void testSkipDirective() {
        consumer = TapConsumerFactory.makeTap13YamlConsumer();

        TestSet testSet = consumer.load(new File(TestDirectives.class
                .getResource("/org/tap4j/parser/issue3406964/ihaveskips.tap")
                .getFile()));
        assertNotNull("Empty Test Set", testSet);
        assertTrue("Wrong number of tests", testSet.getTestResults().size() == 3);

        TestResult tr1 = testSet.getTestResult(1);
        Directive directive = tr1.getDirective();

        assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);

        TestResult tr2 = testSet.getTestResult(2);
        directive = tr2.getDirective();

        assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);
        assertEquals(directive.getReason(), "me too");
        assertEquals(tr2.getDescription(), "- Wrong version in path ");

        TestResult tr3 = testSet.getTestResult(3);
        directive = tr3.getDirective();

        assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);
        assertEquals(directive.getReason(), "well, then...");
    }

    @Test
    public void testTodoDirective() {
        consumer = TapConsumerFactory.makeTap13YamlConsumer();

        TestSet testSet = consumer.load(new File(TestDirectives.class
                .getResource("/org/tap4j/parser/issue3406964/ihavetodoes.tap")
                .getFile()));
        assertNotNull("Empty Test Set", testSet);
        assertTrue("Wrong number of tests", testSet.getTestResults().size() == 2);

        TestResult tr1 = testSet.getTestResult(1);
        Directive directive = tr1.getDirective();

        assertTrue(directive.getDirectiveValue() == DirectiveValues.TODO);

        TestResult tr2 = testSet.getTestResult(2);
        directive = tr2.getDirective();

        assertTrue(directive.getDirectiveValue() == DirectiveValues.TODO);
        assertEquals(directive.getReason(), "configure tail");
        assertEquals(tr2.getDescription(), "");

    }

}
