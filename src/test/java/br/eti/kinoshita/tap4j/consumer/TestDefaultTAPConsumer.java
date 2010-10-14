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
import java.io.PrintWriter;
import java.io.StringWriter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.SkipPlan;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestDefaultTAPConsumer 
extends Assert
{

	protected TapConsumer consumer;
	
	@BeforeMethod
	public void setUp()
	{
		consumer = new DefaultTapConsumer();
	}
	
	@Test
	public void simpleTest()
	{
		try
		{
			consumer.parseLine(null);
		} 
		catch (TapParserException e)
		{
			fail( "Error parsing null line: " + e.getMessage(), e );
		}
	}
	
	// tap1.tap
	@Test
	public void testConsumer_tap1_andPrintDetails()
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_internet/tap1.tap").getFile()) );
			assertTrue( consumer.getNumberOfTestResults() == 2);
			Footer footer = consumer.getFooter();
			assertNull( footer );
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter( writer );
			consumer.printDetails( pw );
			
			StringBuffer sb = writer.getBuffer();
			assertTrue( sb.toString().length() > 0 );
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_internet/output.tap").getFile()) );
			assertTrue( consumer.getNumberOfTestResults() == 10);
			Footer footer = consumer.getFooter();
			assertNull( footer );
			assertTrue( consumer.getNumberOfBailOuts() == 1);
			assertTrue( consumer.getBailOuts().get(0).getReason().equals("Server unavailable!"));
			
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter( writer );
			consumer.printSummary( pw );
			
			StringBuffer sb = writer.getBuffer();
			assertTrue( sb.toString().length() > 0 );
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/comment_planskipall.tap").getFile()) );
			assertTrue( consumer.getNumberOfTestResults() == 0);
			Footer footer = consumer.getFooter();
			assertNull( footer );
			SkipPlan skip = consumer.getPlan().getSkip();
			assertNotNull(skip);
			assertTrue(skip.getReason().equals("Not implemented yet."));
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/header_plan_tr_footer.tap").getFile()) );
			
			assertNotNull( consumer.getHeader() );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( consumer.getNumberOfTestResults() == 2);
			
			assertTrue( consumer.getTestResults().get(0).getDescription().equals("Test 1"));
			
			assertNotNull( consumer.getFooter() );
			
			assertNotNull ( consumer.getFooter().getComment() );
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/header_plan_tr.tap").getFile()) );
			
			assertNotNull( consumer.getHeader() );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( consumer.getNumberOfTestResults() == 2);
			
			assertTrue( consumer.getTestResults().get(0).getDescription().equals("Test 1"));
			
			assertNull( consumer.getFooter() );
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/header_plan.tap").getFile()) );
			
			assertNotNull( consumer.getHeader() );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( consumer.getNumberOfTestResults() == 0);
			
			assertNull( consumer.getFooter() );
		} 
		catch (TapParserException e)
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
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/header_planskipall.tap").getFile()) );
			
			assertNotNull( consumer.getHeader() );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( consumer.getPlan().isSkip() );
			
			assertNotNull( consumer.getPlan().getSkip() );
			
			assertTrue( consumer.getNumberOfTestResults() == 0);
			
			assertNull( consumer.getFooter() );
		} 
		catch (TapParserException e)
		{
			fail("Failed to parse TAP file: " + e.getMessage(), e);
		}
	}
	
	// header_tr_plan.tap
	@Test
	public void testConsumer_header_tr_plan() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/header_tr_plan.tap").getFile()) );
			
			assertTrue( consumer.getTestResults().size() == 2 );
			
			assertNotNull( consumer.getPlan() );
			
			assertFalse( ((DefaultTapConsumer)consumer).isPlanBeforeTestResult() );
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// plan_comment_tr_footer.tap
	@Test
	public void testConsumer_plan_comment_tr_footer() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/plan_comment_tr_footer.tap").getFile()) );
			
			assertNull( consumer.getHeader() );
			
			assertTrue( consumer.getTestResults().size() == 3 );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( ((DefaultTapConsumer)consumer).isPlanBeforeTestResult() );
			
			assertTrue( consumer.getTestResults().size() == consumer.getPlan().getLastTestNumber() );
			
			assertNotNull( consumer.getFooter() );
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// plan_tr.tap
	@Test
	public void testConsumer_plan_tr() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/plan_tr.tap").getFile()) );
			
			assertNull( consumer.getHeader() );
			
			assertTrue( consumer.getTestResults().size() == 2 );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue( ((DefaultTapConsumer)consumer).isPlanBeforeTestResult() );
			
			assertTrue( consumer.getTestResults().size() == consumer.getPlan().getLastTestNumber() );
			
			assertNull( consumer.getFooter() );
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_comment_tr_bailout_header.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_comment_tr_bailout_header() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_comment_tr_bailout_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_header_tr.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_header_tr() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_header_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_plan_header_plan.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_plan_header_plan() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_plan_header_plan.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_plan_tr_header.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_plan_tr_header() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_plan_tr_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_footer.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_tr_footer() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_tr_footer.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_header_header_tr.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_tr_header_header_tr() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_tr_header_header_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_tr_plan_header.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_tr_plan_header() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_tr_plan_header.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
		{
			throw e;
		}
	}
	
	// invalid_tr.tap
	@Test(expectedExceptions=TapParserException.class)
	public void testConsumer_invalid_tr() 
	throws TapParserException
	{
		try
		{
			consumer.parseFile( new File(TestDefaultTAPConsumer.class.getResource("/input_tap4j/invalid_tr.tap").getFile()) );
			
			fail("Not supposed to get here.");
		} 
		catch (TapParserException e)
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
			consumer.parseTapStream( tapStream.toString() );
			
			assertTrue( consumer.getPlan().getLastTestNumber() == 1 );
			
			assertNotNull( consumer.getHeader() );
			
			assertNotNull( consumer.getHeader().getComment() );
			
			assertEquals( consumer.getBailOuts().get(0).getReason(), "Out of memory exception ");
			
			assertEquals( consumer.getBailOuts().get(0).getComment().getText(), "Contact admin! 9988" );
			
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter( writer );
			consumer.printDetails( pw );
			
			StringBuffer sb = writer.getBuffer();
			assertTrue( sb.toString().length() > 0 );
		} 
		catch (TapParserException e)
		{
			fail("Failed to parse TAP stream: " + e.getMessage(), e);
		}
	}
	
}
