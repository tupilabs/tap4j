/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
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
package br.eti.kinoshita.tap4j.producer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.model.Comment;
import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTapProducer 
extends Assert
{
	
	private static final Integer TAP_VERSION = 13;
	private TapProducer tapProducer;
	
	// Temp file to where we output the generated tap stream.
	private File tempFile;
	
	private static final Integer INITIAL_TEST_STEP = 1;
	
	@BeforeTest
	public void setUp()
	{
		tapProducer = new DefaultTapProducer( );
		Header header = new Header( TAP_VERSION );
		tapProducer.setHeader(header);
		Plan plan = new Plan(INITIAL_TEST_STEP, 3);
		tapProducer.setPlan(plan);
		Comment singleComment = new Comment( "Starting tests" );
		tapProducer.addComment( singleComment );
		
		TestResult tr1 = new TestResult(StatusValues.OK, 1);
		LinkedHashMap<String, Object> diagnostic = new LinkedHashMap<String, Object>();
		diagnostic.put("file", "testingproducer.txt");
		diagnostic.put("time", System.currentTimeMillis());
		diagnostic.put("Tester", "Bruno P. Kinoshita");
		LinkedHashMap<String, Object> map2 = new LinkedHashMap<String, Object>();
		map2.put("EHCTA", 1233);
		map2.put("TRANSACTION", 3434);
		diagnostic.put("Audit", map2);
		tr1.setDiagnostic( diagnostic );
		//tr1.setDiagnostic(diagnostic)
		tapProducer.addTestResult(tr1);
		
		TestResult tr2 = new TestResult(StatusValues.NOT_OK, 2);
		tr2.setTestNumber(2);
		tapProducer.addTestResult(tr2);
		
		tapProducer.setFooter( new Footer("End") );
		
		try
		{
			tempFile = File.createTempFile("tap4j_", ".tap");
		} 
		catch (IOException e)
		{
			fail("Failed to create temp file: " + e.getMessage(), e);
		}
	}
	
	@Test
	public void testTapProducer()
	{
		assertTrue ( tapProducer.getTapLines().size() > 0 );
		
		try
		{
			tapProducer.printTo( tempFile );
			
			System.out.println(tempFile);
		}
		catch ( Exception e  )
		{
			fail("Failed to print TAP Stream into file.", e);
		}
		
//		BufferedReader reader = null;
//		
//		try
//		{
//			reader = new BufferedReader( new FileReader( tempFile ) );
//			
//			String line = null;
//			
//			while ( (line = reader.readLine()) != null )
//			{
//				System.out.println(line);
//			}
//		}
//		catch (Exception e)
//		{
//			fail("Failed to read temp file.", e);
//		} 
//		finally 
//		{
//			if ( reader != null )
//			{
//				try
//				{
//					reader.close();
//				} catch (Exception e2)
//				{
//					e2.printStackTrace(System.err);
//				}
//				reader = null;
//			}
//		}
	}
}