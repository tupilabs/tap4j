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
extends Assert
{

	protected TapConsumer consumer;
	
	@BeforeMethod
	public void setUp()
	{
		consumer = new TapConsumerImpl();
	}
	
	// tap1.tap
	@Test
	public void testConsumer_tap1_andPrintDetails()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_internet/tap1.tap").getFile()) );
			assertNotNull( testSet );
			assertTrue( testSet.getNumberOfTestResults() == 2);
			Footer footer = testSet.getFooter();
			assertNull( footer );
			String details = testSet.getDetails();
			
			assertTrue( details.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
		
	}
	
	// output.tap
	@Test
	public void testConsumer_output_andPrintSummary()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_internet/output.tap").getFile()) );
			assertTrue( testSet.getNumberOfTestResults() == 10);
			Footer footer = testSet.getFooter();
			assertNull( footer );
			assertTrue( testSet.getNumberOfBailOuts() == 1);
			assertTrue( testSet.getBailOuts().get(0).getReason().equals("Server unavailable!"));
			
			String summary = testSet.getSummary();
			
			assertTrue( summary.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// comment_planskipall.tap
	@Test
	public void testConsumer_comment_planskipall()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/comment_planskipall.tap").getFile()) );
			assertTrue( testSet.getNumberOfTestResults() == 0);
			Footer footer = testSet.getFooter();
			assertNull( footer );
			SkipPlan skip = testSet.getPlan().getSkip();
			assertNotNull(skip);
			assertTrue(skip.getReason().equals("Not implemented yet."));
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_plan_tr_footer.tap
	@Test
	public void testConsumer_header_plan_tr_footer()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan_tr_footer.tap").getFile()) );
			
			assertNotNull( testSet.getHeader() );
			
			assertNotNull( testSet.getPlan() );
			
			assertTrue( testSet.getNumberOfTestResults() == 2);
			
			assertTrue( testSet.getTestResults().get(0).getDescription().equals("Test 1"));
			
			assertNotNull( testSet.getFooter() );
			
			assertNotNull ( testSet.getFooter().getComment() );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_plan_tr.tap
	@Test
	public void testConsumer_header_plan_tr()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan_tr.tap").getFile()) );
			
			assertNotNull( testSet.getHeader() );
			
			assertNotNull( testSet.getPlan() );
			
			assertTrue( testSet.getNumberOfTestResults() == 2);
			
			assertTrue( testSet.getTestResults().get(0).getDescription().equals("Test 1"));
			
			assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_plan.tap
	@Test
	public void testConsumer_header_plan()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_plan.tap").getFile()) );
			
			assertNotNull( testSet.getHeader() );
			
			assertNotNull( testSet.getPlan() );
			
			assertTrue( testSet.getNumberOfTestResults() == 0);
			
			assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_planskipall.tap
	@Test
	public void testConsumer_header_planskipall()
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_planskipall.tap").getFile()) );
			
			assertNotNull( testSet.getHeader() );
			
			assertNotNull( testSet.getPlan() );
			
			assertTrue( testSet.getPlan().isSkip() );
			
			assertNotNull( testSet.getPlan().getSkip() );
			
			assertTrue( testSet.getNumberOfTestResults() == 0);
			
			assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_tr_plan.tap
	@Test
	public void testConsumer_header_tr_plan() 
	throws TapConsumerException
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/header_tr_plan.tap").getFile()) );
			
			assertTrue( testSet.getTestResults().size() == 2 );
			
			assertNotNull( testSet.getPlan() );
			
			//assertFalse( ((Tap13YamlParser)consumer).isPlanBeforeTestResult() );
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// plan_comment_tr_footer.tap
	@Test
	public void testConsumer_plan_comment_tr_footer() 
	throws TapConsumerException
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/plan_comment_tr_footer.tap").getFile()) );
			
			assertNull( testSet.getHeader() );
			
			assertTrue( testSet.getTestResults().size() == 3 );
			
			assertNotNull( testSet.getPlan() );
			
			//assertTrue( ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
			
			assertTrue( testSet.getTestResults().size() == testSet.getPlan().getLastTestNumber() );
			
			assertNotNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// plan_tr.tap
	@Test
	public void testConsumer_plan_tr() 
	throws TapConsumerException
	{
		try
		{
			TestSet testSet = consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/plan_tr.tap").getFile()) );
			
			assertNull( testSet.getHeader() );
			
			assertTrue( testSet.getTestResults().size() == 2 );
			
			assertNotNull( testSet.getPlan() );
			
			// assertTrue( ((TapConsumerImpl)consumer).isPlanBeforeTestResult() );
			
			assertTrue( testSet.getTestResults().size() == testSet.getPlan().getLastTestNumber() );
			
			assertNull( testSet.getFooter() );
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_comment_tr_bailout_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_comment_tr_bailout_header() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_comment_tr_bailout_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_header_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_header_tr() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_header_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_plan_header_plan.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_plan_header_plan() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_plan_header_plan.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_plan_tr_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_plan_tr_header() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_plan_tr_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_footer.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_tr_footer() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_footer.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_header_header_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_tr_header_header_tr() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_header_header_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_plan_header.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_tr_plan_header() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr_plan_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	// invalid_tr.tap
	@Test(expectedExceptions=TapConsumerException.class)
	public void testConsumer_invalid_tr() 
	throws TapConsumerException
	{
		try
		{
			consumer.load( new File(TestTap13Consumer.class.getResource("/input_tap4j/invalid_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapConsumerException e)
		{
			throw e;
		}
	}
	
	@Test
	public void testConsumer_tapStream1_andPrintDetails()
	{
		StringBuffer tapStream = new StringBuffer();
		
		tapStream.append("TAP version 13 # the header\n");
		tapStream.append("1..1\n");
		tapStream.append("ok 1\n");
		tapStream.append("Bail out! Out of memory exception # Contact admin! 9988\n");
		
		try
		{
			TestSet testSet = consumer.load( tapStream.toString() );
			
			assertTrue( testSet.getPlan().getLastTestNumber() == 1 );
			
			assertNotNull( testSet.getHeader() );
			
			assertNotNull( testSet.getHeader().getComment() );
			
			assertEquals( testSet.getBailOuts().get(0).getReason(), "Out of memory exception ");
			
			assertEquals( testSet.getBailOuts().get(0).getComment().getText(), "Contact admin! 9988" );
			
			String details = testSet.getDetails();
			
			assertTrue( details.length() > 0 );
		} 
		catch (TapConsumerException e)
		{
			fail("Failed to parse TAP stream: " + e.getMessage(), e);
		}
	}
	
}
