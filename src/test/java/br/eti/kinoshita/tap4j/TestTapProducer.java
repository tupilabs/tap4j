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
package br.eti.kinoshita.tap4j;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTapProducer 
extends Assert
{
	
	private static final Integer TAP_VERSION = 13;
	private TapProducer tapProducer;
	
	@BeforeMethod
	public void setUp()
	{
		tapProducer = new DefaultTapProducer( TAP_VERSION );
	}
	
	@Test
	public void testTapProducer()
	{
		tapProducer.setSkipAll(true, "Testing...");
		for ( int i = 0 ; i < 10 ; i++ )
		{
			final TestResult testResult = this.makeRandomTestResult( i );
			if ( i == 3 ) 
			{
				this.tapProducer.addComment( "Sample comment" );
			} 
			if ( i == 6 )
			{
				this.tapProducer.addBailOut( "Server unavailable!" );
				((DefaultTapProducer)this.tapProducer).addText("Text to be ignored");
			}
			if ( i == 1 )
			{
				testResult.addDirective(Directive.SKIP, "Always skip");
			}
			this.tapProducer.addTestResult( testResult );
		}
		
		this.tapProducer.setFooter( "Simple footer." );
		String tapStream = tapProducer.getTapStream();
		
		System.out.println( tapStream );
		
		try
		{
			((DefaultTapProducer)tapProducer).printTo( new File("output.t") );
		} catch (IOException e)
		{
			fail(e.getMessage(), e);
		}
	}
	
	/**
	 * Makes a random Test Result for the tests.
	 * 
	 * @return Random Test Result
	 */
	protected TestResult makeRandomTestResult( int testNumber )
	{
		final TestResult testResult = new TestResult();
		
		testResult.setTestNumber( ++testNumber );
		if ( testNumber % 2 == 0 )
		{
			testResult.setStatus( Status.OK );
			testResult.setResult( "Test passed OK!" );
			testResult.addDirective(Directive.TODO, "We'll implement this in the next release.");
		} 
		else 
		{
			testResult.setStatus( Status.NOT_OK );
			testResult.setResult( "Failed to execute IO operation." );
			testResult.setComment( "Reviewing" );
		}
		
		return testResult;
	}
	
	/**
	 * Tries to reproduce a TAP file found on Internet (tap1.t)
	 */
	@Test
	public void reproduceTap1()
	{
		((DefaultTapProducer)tapProducer).addText("Error at line 12");
		TestResult result = new TestResult();
		result.setTestNumber(1);
		result.setStatus(Status.OK);
		tapProducer.addTestResult(result);
		result= new TestResult();
		result.setTestNumber(2);
		result.setStatus(Status.OK);
		result.setComment("BANG");
		tapProducer.addTestResult(result);
		System.out.println(tapProducer.getTapStream());
		
		try
		{
			((DefaultTapProducer)tapProducer).printTo(new File("_tap1.t"));
		} catch (IOException e)
		{
			fail(e.getMessage(), e);
		}
	}
	
}
