/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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

import java.io.File;

import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.Tap13YamlParser;
import org.tap4j.util.DirectiveValues;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTap13YamlConsumer2 {

	@Test
	public void testTapConsumerYaml() {

		TapConsumer consumer = new TapConsumerImpl(new Tap13YamlParser());

		TestSet testSet = consumer
		        .load(new File(
		                TestTap13YamlConsumer2.class
		                        .getResource(
		                                "/input_tap13/tap_with_yaml_comments_bailout_directives.tap")
		                        .getFile()));

		Assert.assertNotNull(testSet);

		Plan plan = testSet.getPlan();
		Assert.assertTrue(plan.getInitialTestNumber() == 1);
		Assert.assertTrue(plan.getLastTestNumber() == 3);
		Assert.assertTrue(testSet.getNumberOfTestResults() == 3);

		Assert.assertTrue(testSet.containsBailOut());

		TestResult testNumber2WithSkipDirective = testSet.getTestResult(Integer
		        .valueOf(2));
		Assert.assertNotNull(testNumber2WithSkipDirective);

		Directive skipDirective = testNumber2WithSkipDirective.getDirective();
		Assert.assertTrue(skipDirective.getDirectiveValue() == DirectiveValues.SKIP);
		Assert.assertTrue(skipDirective.getReason().equals(
		        "not implemented yet"));

		Assert.assertNotNull(testSet.getFooter());

	}

	@Test(expectedExceptions = { TapConsumerException.class })
	public void testDiagnosticWithoutLastParsedElement() {
		TapConsumer consumer = new TapConsumerImpl(new Tap13YamlParser());

		consumer.load(new File(
		        TestTap13YamlConsumer2.class
		                .getResource(
		                        "/input_tap13/tap_with_diagnostic_and_without_lastparsedtestresult.tap")
		                .getFile()));
	}

	@Test(expectedExceptions = { TapConsumerException.class })
	public void testDiagnosticWithWrongIndentation() {
		TapConsumer consumer = new TapConsumerImpl(new Tap13YamlParser());

		consumer.load(new File(TestTap13YamlConsumer2.class.getResource(
		        "/input_tap13/tap_with_diagnostic_and_wrong_indentation.tap")
		        .getFile()));
	}

}
