package org.tap4j.parser.issue3406964;

import java.io.File;

import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for issue 3406964.
 * 
 * The skip directive in TAP can contain both upper and lower case SKIP and TO
 * DO text. However tap4j is considering only the upper case version.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
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
		Assert.assertNotNull(testSet, "Empty Test Set");
		Assert.assertTrue(testSet.getTestResults().size() == 3,
		        "Wrong number of tests");

		TestResult tr1 = testSet.getTestResult(1);
		Directive directive = tr1.getDirective();

		Assert.assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);

		TestResult tr2 = testSet.getTestResult(2);
		directive = tr2.getDirective();

		Assert.assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);
		Assert.assertEquals(directive.getReason(), "me too");
		Assert.assertEquals(tr2.getDescription(), "- Wrong version in path ");

		TestResult tr3 = testSet.getTestResult(3);
		directive = tr3.getDirective();

		Assert.assertTrue(directive.getDirectiveValue() == DirectiveValues.SKIP);
		Assert.assertEquals(directive.getReason(), "well, then...");
	}

	@Test
	public void testTodoDirective() {
		consumer = TapConsumerFactory.makeTap13YamlConsumer();

		TestSet testSet = consumer.load(new File(TestDirectives.class
		        .getResource("/org/tap4j/parser/issue3406964/ihavetodoes.tap")
		        .getFile()));
		Assert.assertNotNull(testSet, "Empty Test Set");
		Assert.assertTrue(testSet.getTestResults().size() == 2,
		        "Wrong number of tests");

		TestResult tr1 = testSet.getTestResult(1);
		Directive directive = tr1.getDirective();

		Assert.assertTrue(directive.getDirectiveValue() == DirectiveValues.TODO);

		TestResult tr2 = testSet.getTestResult(2);
		directive = tr2.getDirective();

		Assert.assertTrue(directive.getDirectiveValue() == DirectiveValues.TODO);
		Assert.assertEquals(directive.getReason(), "configure tail");
		Assert.assertEquals(tr2.getDescription(), "");

	}

}
