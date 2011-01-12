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
package br.eti.kinoshita.tap4j.consumer;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.SkipPlan;
import br.eti.kinoshita.tap4j.model.TestSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTap13Consumer 
{//NOPMD

	/**
	 * 
	 */
	private static final String FAILED_TO_PARSE_TAP_FILE = "Failed to parse TAP file: ";
	/**
	 * 
	 */
	private static final String NOT_SUPPOSED_TO_GET_HERE = "Not supposed to get here.";
	protected TapConsumer consumer;
	
	@BeforeMethod
	public void setUp()
	{
		consumer = new TapConsumerImpl();
	}
	
	// tap1.tap
	@Test
	public void testConsumeAndPrintDetails()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_internet/tap1.tap").getFile()) );
			Assert.assertNotNull( testSet );
			Assert.assertTrue( testSet.getNumberOfTestResults() == 2);
			Footer footer = testSet.getFooter();
			Assert.assertNull( footer );
			String details = testSet.getDetails();
			
			Assert.assertTrue( details.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail(FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
		
	}
	
	// output.tap
	@Test
	public void testConsumerAndPrintSummary()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_internet/output.tap").getFile()) );
			Assert.assertTrue( testSet.getNumberOfTestResults() == 10);
			Footer footer = testSet.getFooter();
			Assert.assertNull( footer );
			Assert.assertTrue( testSet.getNumberOfBailOuts() == 1);
			Assert.assertTrue( testSet.getBailOuts().get(0).getReason().equals("Server unavailable!"));
			
			String summary = testSet.getSummary();
			
			Assert.assertTrue( summary.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail(FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// comment_planskipall.tap
	@Test
	public void testConsumerPlanskipall()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/comment_planskipall.tap").getFile()) );
			Assert.assertTrue( testSet.getNumberOfTestResults() == 0);
			Footer footer = testSet.getFooter();
			Assert.assertNull( footer );
			SkipPlan skip = testSet.getPlan().getSkip();
			Assert.assertNotNull(skip);
			Assert.assertTrue(skip.getReason().equals("Not implemented yet."));
		} 
		catch (TapConsumerException e)
		{
			Assert.fail(FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// header_plan_tr_footer.tap
	@Test
	public void testConsumerHeaderPlanTrFooter()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan_tr_footer.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue( testSet.getNumberOfTestResults() == 2);
			
			Assert.assertTrue( testSet.getTestResults().get(0).getDescription().equals("Test 1"));
			
			Assert.assertNotNull( testSet.getFooter() );
			
			Assert.assertNotNull ( testSet.getFooter().getComment() );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail(FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// header_plan_tr.tap
	@Test
	public void testConsumerHeaderPlanTr()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan_tr.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue( testSet.getNumberOfTestResults() == 2);
			
			Assert.assertTrue( testSet.getTestResults().get(0).getDescription().equals("Test 1"));
			
			Assert.assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail( FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// header_plan.tap
	@Test
	public void testConsumerHeaderPlan()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue( testSet.getNumberOfTestResults() == 0);
			
			Assert.assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail( FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// header_planskipall.tap
	@Test
	public void testConsumerHeaderPlanskipall()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_planskipall.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue( testSet.getPlan().isSkip() );
			
			Assert.assertNotNull( testSet.getPlan().getSkip() );
			
			Assert.assertTrue( testSet.getNumberOfTestResults() == 0);
			
			Assert.assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail( FAILED_TO_PARSE_TAP_FILE + e.getMessage(), e);
		}
	}
	
	// header_tr_plan.tap
	@Test
	public void testConsumerHeaderTrPlan() 
	throws TapConsumerException
	{
		TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_tr_plan.tap").getFile()) );
		
		Assert.assertTrue( testSet.getTestResults().size() == 2 );
		
		Assert.assertNotNull( testSet.getPlan() );
		
		//Assert.assertFalse( ((Tap13YamlParser)consumer).isPlanBeforeTestResult() );
	}
	
	// plan_comment_tr_footer.tap
	@Test
	public void testConsumerPlanCommentTrFooter() 
	throws TapConsumerException
	{
		TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/plan_comment_tr_footer.tap").getFile()) );
		
		Assert.assertNull( testSet.getHeader() );
		
		Assert.assertTrue( testSet.getTestResults().size() == 3 );
		
		Assert.assertNotNull( testSet.getPlan() );
		
		//Assert.assertTrue( ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
		
		Assert.assertTrue( testSet.getTestResults().size() == testSet.getPlan().getLastTestNumber() );
		
		Assert.assertNotNull( testSet.getFooter() );
	}
	
	// plan_tr.tap
	@Test
	public void testConsumerPlanTr() 
	throws TapConsumerException
	{
		TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/plan_tr.tap").getFile()) );
		
		Assert.assertNull( testSet.getHeader() );
		
		Assert.assertTrue( testSet.getTestResults().size() == 2 );
		
		Assert.assertNotNull( testSet.getPlan() );
		
		// Assert.assertTrue( ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
		
		Assert.assertTrue( testSet.getTestResults().size() == testSet.getPlan().getLastTestNumber() );
		
		Assert.assertNull( testSet.getFooter() );
	}
	
	// invalid_comment_tr_bailout_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidCommentTrBailoutHeader() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_comment_tr_bailout_header.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_header_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidHeaderTr() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_header_tr.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_plan_header_plan.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidPlanHeaderPlan() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_plan_header_plan.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_plan_tr_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidPlanTrHeader() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_plan_tr_header.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_tr_footer.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidTrFooter() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_footer.tap").getFile()) );
			
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_tr_header_header_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidTrHeaderHeaderTr() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_header_header_tr.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_tr_plan_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidTrPlanHeader() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_plan_header.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	// invalid_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumerInvalidTr() 
	throws TapConsumerException
	{
		consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr.tap").getFile()) );
		
		Assert.fail( NOT_SUPPOSED_TO_GET_HERE);
	}
	
	@Test
	public void testConsumerTapStream1AndPrintDetails()
	{
		StringBuilder tapStream = new StringBuilder();
		
		tapStream.append("TAP version 13 # the header\n");
		tapStream.append("1..1\n");
		tapStream.append("ok 1\n");
		tapStream.append("Bail out! Out of memory exception # Contact admin! 9988\n");
		
		try
		{
			TestSet testSet = consumer.load( tapStream.toString() );
			
			Assert.assertTrue( testSet.getPlan().getLastTestNumber() == 1 );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getHeader().getComment() );
			
			Assert.assertEquals( testSet.getBailOuts().get(0).getReason(), "Out of memory exception ");
			
			Assert.assertEquals( testSet.getBailOuts().get(0).getComment().getText(), "Contact admin! 9988" );
			
			String details = testSet.getDetails();
			
			Assert.assertTrue( details.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			Assert.fail( "Failed to parse TAP stream: " + e.getMessage(), e);
		}
	}
	
}
