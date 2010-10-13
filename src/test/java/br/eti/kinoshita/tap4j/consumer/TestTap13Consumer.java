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
import java.util.Iterator;
import java.util.LinkedHashMap;

import junit.framework.Assert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTap13Consumer 
extends Assert
{

	protected Tap13Consumer consumer;
	
	@BeforeMethod
	public void setUp()
	{
		consumer = new Tap13Consumer();
	}
	
	@Test
	public void test1()
	{
		try 
		{
			consumer.parseFile( new File(TestTap13Consumer.class.getResource("/input_tap13/1.tap").getFile()) );
			
			assertNotNull( consumer.getHeader() );
			
			assertTrue ( consumer.getHeader().getVersion() == 13 );
			
			assertNotNull( consumer.getPlan() );
			
			assertTrue ( consumer.getPlan().getInitialTestNumber() == 1 );
			
			assertTrue( consumer.getPlan().getLastTestNumber() == 3 );
			
			assertTrue( consumer.getTestResults().size() == 3 );
			
			assertTrue ( consumer.getTestResult(1).getStatus() == StatusValues.OK );
			
			assertNull ( consumer.getTestResult(1).getDiagnostic() );
			
			assertTrue ( consumer.getTestResult(2).getStatus() == StatusValues.NOT_OK );
			
			Iterable<?> diagnostic = consumer.getTestResult(2).getDiagnostic();
			
			assertNotNull( diagnostic );
			
			Iterator<?> mapIt = diagnostic.iterator();
			LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>)mapIt.next();
			assertEquals( map.get("file"), "t/something.t" );
			
			assertTrue ( consumer.getTestResult(3).getStatus() == StatusValues.OK );
			
		} 
		catch (TapParserException e) 
		{
			fail("" + e.getMessage());
		}
	}
	
}
