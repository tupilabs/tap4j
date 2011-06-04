package org.tap4j.consumer.issue3311330;

import java.io.File;

import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.consumer.TestTap13YamlConsumer2;
import org.tap4j.model.TestSet;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0.4
 */
public class TestTap13YamlConsumerWithMultipleFiles
{
	
	private TapConsumer consumer;
	
	@Test
	public void testConsumerWithMultipleFiles()
	{
		consumer = TapConsumerFactory.makeTap13YamlConsumer();
		
		TestSet testSet = consumer.load(new File(TestTap13YamlConsumer2.class.getResource("/org/tap4j/consumer/issue3311330/1.tap").getFile()));
		Assert.assertNotNull ( testSet, "Empty Test Set" );
		Assert.assertTrue( testSet.getTestResults().size() == 2, "Wrong number of tests" );
		
		testSet = consumer.load(new File(TestTap13YamlConsumer2.class.getResource("/org/tap4j/consumer/issue3311330/fala.tap").getFile()));
		Assert.assertNotNull ( testSet, "Empty Test Set" );
		Assert.assertTrue( testSet.getTestResults().size() == 3, "Wrong number of tests" );
		
		testSet = consumer.load(new File(TestTap13YamlConsumer2.class.getResource("/org/tap4j/consumer/issue3311330/oi.tap").getFile()));
		Assert.assertNotNull ( testSet, "Empty Test Set" );
		Assert.assertTrue( testSet.getTestResults().size() == 1, "Wrong number of tests" );
	}

}
