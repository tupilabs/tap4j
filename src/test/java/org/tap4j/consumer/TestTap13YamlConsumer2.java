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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;

/**
 * @since 1.0
 */
public class TestTap13YamlConsumer2 extends BaseTapTest {

    @Test
    public void testTapConsumerYaml() {
        final TestSet testSet = getTestSet("/org/tap4j/consumer/tap_with_yaml_comments_bailout_directives.tap");
        assertNotNull(testSet);
        final Plan plan = testSet.getPlan();
        assertEquals(1, (int) plan.getInitialTestNumber());
        assertEquals(3, (int) plan.getLastTestNumber());
        assertEquals(3, testSet.getNumberOfTestResults());
        assertTrue(testSet.containsBailOut());
        final TestResult testNumber2WithSkipDirective = testSet.getTestResult(2);
        assertNotNull(testNumber2WithSkipDirective);
        final Directive skipDirective = testNumber2WithSkipDirective.getDirective();
        assertSame(skipDirective.getDirectiveValue(), DirectiveValues.SKIP);
        assertEquals("not implemented yet", skipDirective.getReason());
        assertNotNull(testSet.getFooter());
    }

    @Test
    public void testDiagnosticWithoutLastParsedElement() {
        Assertions.assertThrows(TapConsumerException.class, () -> getTestSet("/org/tap4j/consumer/tap_with_diagnostic_and_without_lastparsedtestresult.tap"));
    }

    @Test
    public void testDiagnosticWithWrongIndentation() {
        Assertions.assertThrows(TapConsumerException.class, () -> getTestSet("/org/tap4j/consumer/tap_with_diagnostic_and_wrong_indentation.tap"));
    }

}
