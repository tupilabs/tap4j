/*
 * The MIT License
 *
 * Copyright (c) <2010> <Bruno P. Kinoshita>
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
package br.eti.kinoshita.tap4j;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.consumer.TapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapConsumerException;
import br.eti.kinoshita.tap4j.consumer.TapConsumerImpl;
import br.eti.kinoshita.tap4j.model.BailOut;
import br.eti.kinoshita.tap4j.model.Comment;
import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.producer.TapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducerImpl;
import br.eti.kinoshita.tap4j.representer.Tap13YamlRepresenter;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * Test where the producer outputs a tap file and then a consumer reads it and 
 * checks if the values are correct. For example, you create a test with a Test 
 * Result with a String there. Then you use the consumer to read the tap file 
 * created and check if the consumer can read the String. Voila.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @author Cesar Fernandes de Almeida
 * 
 * @since 1.0
 */
public class TestProduceConsume
{
	private static final Integer TAP_VERSION = 13;
	private TapProducer tapProducer;
	private TapConsumer tapConsumer;
	
	private TestSet testSet;
	
	// Temp file to where we output the generated tap stream.
	private File tempFile;
	
	private static final Integer INITIAL_TEST_STEP = 1;
	
	@BeforeTest
	public void setUp()
	{
		tapProducer = new TapProducerImpl( new Tap13YamlRepresenter() );
		tapConsumer = new TapConsumerImpl();
		
		testSet = new TestSet();
		
		Header header = new Header( TAP_VERSION );
		testSet.setHeader(header);
		
		Plan plan = new Plan(INITIAL_TEST_STEP, 3);
		Comment commentPlan = new Comment("Testing something with a plan that I do not know exactly what it is about!");
		plan.setComment(commentPlan);
		testSet.setPlan(plan);
		
		Comment singleComment = new Comment( "Starting tests" );
		testSet.addComment( singleComment );
		
		TestResult tr1 = new TestResult(StatusValues.OK, 1);
		testSet.addTestResult(tr1);
		
		TestResult tr2 = new TestResult();
		tr2.setStatus(StatusValues.NOT_OK);
		tr2.setTestNumber(testSet.getNextTestNumber());
		testSet.addTestResult(tr2);
		
		TestResult tr3 = new TestResult(StatusValues.OK, 3);
		Comment commentTr3 = new Comment("Test 3 :)");
		tr3.setComment(commentTr3);
		testSet.addTestResult(tr3);
		
		testSet.setFooter( new Footer("End") );
		
		try
		{
			tempFile = File.createTempFile("tap4j_", ".tap");
		} 
		catch (IOException e)
		{
			Assert.fail("Failed to create temp file: " + e.getMessage(), e);
		}
	}
	
	@Test
	public void testTapProducer()
	{
		Assert.assertTrue ( testSet.getTapLines().size() > 0 );
		
		try
		{
			tapProducer.dump( testSet, tempFile );
		}
		catch ( Exception e  )
		{
			Assert.fail("Failed to print TAP Stream into file.", e);
		}
	}
	
	
	@Test( dependsOnMethods = {"testTapProducer"} )
	public void testConsumer()
	{
		try
		{
			TestSet testSet = tapConsumer.load( tempFile );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue( testSet.getNumberOfTestResults() == 3);
			
			Assert.assertNotNull( testSet.getFooter() );
			
			Assert.assertTrue( testSet.getTapLines().size() > 0 );
			
			Assert.assertTrue( testSet.getNumberOfTapLines() > 0 );
			
			Assert.assertTrue( testSet.containsOk() );
			
			Assert.assertFalse( testSet.containsBailOut() );
			
			Assert.assertTrue( testSet.containsNotOk() );
			
			Assert.assertTrue( testSet.getComments().size() > 0 );
			
			Assert.assertTrue( testSet.getNumberOfComments() > 0 );
			
			Assert.assertTrue( testSet.getComments().size() == testSet.getNumberOfComments() );
			
			Assert.assertNotNull( tapConsumer.getTestSet());
			
			Assert.assertEquals( testSet.getTestResult(1).getStatus(), StatusValues.OK );
			
		} 
		catch (TapConsumerException e)
		{
			Assert.fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	@Test
	public void testWithBailOut()
	{
		BailOut bailOut = new BailOut(null);
		
		tapConsumer.getTestSet().getBailOuts().add( bailOut );
		
		Assert.assertTrue( tapConsumer.getTestSet().containsBailOut() );
	}
}
