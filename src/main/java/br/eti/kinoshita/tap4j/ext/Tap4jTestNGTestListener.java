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
package br.eti.kinoshita.tap4j.ext;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import br.eti.kinoshita.tap4j.model.Directive;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.SkipPlan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.producer.DefaultTapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducer;
import br.eti.kinoshita.tap4j.util.DirectiveValues;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * Provides integration with TestNG through the TestListenerAdapter class.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Tap4jTestNGTestListener 
extends TestListenerAdapter 
{

	/**
	 * Tap Output Directory. 
	 */
	public static final String TAP_OUTPUT_DIRECTORY = "tap";

	/**
	 * Version of Tap that we used.
	 */
	private static final Integer TAP_VERSION = 13;

	/**
	 * Produces Tap Streams.
	 */
	protected TapProducer tapProducer = new DefaultTapProducer();
	
	/**
	 * Counter for number of tests.
	 */
	protected static int counter = 0;

	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext testContext)
	{			

		try
		{
			this.forceCreateTapDirectory( TAP_OUTPUT_DIRECTORY );
		} 
		catch (IOException e)
		{
			throw new RuntimeException("Failed to create tap output directory [" + new File( TAP_OUTPUT_DIRECTORY ) + "]. Exception message: " + e.getMessage(), e);
		}
		
		this.tapProducer.setHeader(new Header(TAP_VERSION));		
		int numberOfTests = testContext.getAllTestMethods().length;
		Plan tapPlan;
		if ( numberOfTests == 0 )
		{
			
			SkipPlan skipPlan = new SkipPlan("no tests found.");
			tapPlan = new Plan(numberOfTests, skipPlan);
		} 
		else
		{
			tapPlan = new Plan(numberOfTests);
		}
		this.tapProducer.setPlan(tapPlan);
	}
	
	/**
	 * Force creation of the tap directory. Throws an IOException if any 
	 * error occurs.
	 * 
	 * @param tapOutputDirName Tap Output Directory name
	 * @return true if the file was successfully created, false otherwise
	 * @throws IOException
	 */
	private void forceCreateTapDirectory( String tapOutputDirName ) 
	throws IOException
	{
		File directory = new File( tapOutputDirName );
		
		if (directory.exists()) 
		{
			if (!directory.isDirectory()) 
			{
				String message =
					 "File "
					 + directory
					 + " exists and is "
					 + "not a directory. Unable to create directory.";
				throw new IOException(message);
			}
		}
		else 
		{
			// Double-check that some other thread or process hasn't made
			// the directory in the background
			if (!directory.mkdirs() && !directory.isDirectory()) 
			{
				String message =
					"Unable to create directory " + directory;
				throw new IOException(message);
			}
		} 
		
	}

	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult tr) 
	{
		counter+=1;
		TestResult testResult = new TestResult(StatusValues.OK, counter);
		testResult.setDescription( tr.getMethod().getMethodName() );
		this.tapProducer.addTestResult( testResult );
	}
	
	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult tr) 
	{
		counter+=1;
		TestResult testResult = new TestResult(StatusValues.NOT_OK, counter);
				
		this.makeDiagnostic ( tr, testResult );
		
		this.tapProducer.addTestResult( testResult );
		
	}
	
	/**
	 * @param testNgTestResult
	 * @param tapTestResult
	 */
	protected void makeDiagnostic( ITestResult testNgTestResult, TestResult tapTestResult )
	{
		LinkedHashMap<String, Object> diagnostic = new LinkedHashMap<String, Object>();
		
		diagnostic.put("file", testNgTestResult.getTestClass().getName() + ".java" );
		diagnostic.put("description", testNgTestResult.getMethod().getDescription() );
		
		this.maybeAddWantedFound( testNgTestResult, diagnostic );
		
		this.addExtensions( testNgTestResult, diagnostic );
		
		tapTestResult.setDiagnostic( diagnostic );
	}

	/**
	 * @param testNgTestResult
	 * @param diagnostic
	 */
	protected void maybeAddWantedFound( ITestResult testNgTestResult,
			LinkedHashMap<String, Object> diagnostic )
	{
		Throwable throwable = testNgTestResult.getThrowable();
		String exceptionText = ExtUtil.fromThrowableToString(throwable);
		
		String wanted = ExtUtil.maybeRetrieveTestNGWanted(exceptionText);
		if ( StringUtils.isNotEmpty( wanted ) )
		{
			String found = ExtUtil.maybeRetrieveTestNGFound( exceptionText );
			diagnostic.put("wanted", wanted);
			diagnostic.put("found", found);
		}
		else
		{
			wanted = ExtUtil.maybeRetriveTestNGWantedException( exceptionText );
			if ( StringUtils.isNotEmpty( wanted ) )
			{
				String found = ExtUtil.maybeRetriveTestNGFoundException( exceptionText );
				diagnostic.put("wanted", wanted);
				diagnostic.put("found", found);
			}
		}
	}

	/**
	 * @param testNgTestResult
	 * @param diagnostic
	 */
	protected void addExtensions( ITestResult testNgTestResult,
			LinkedHashMap<String, Object> diagnostic )
	{
		LinkedHashMap<String, Object> extensionsMap = new LinkedHashMap<String, Object>();
		
		extensionsMap.put("Start", testNgTestResult.getStartMillis());
		extensionsMap.put("End", testNgTestResult.getEndMillis());
		extensionsMap.put("Took", testNgTestResult.getEndMillis() - testNgTestResult.getStartMillis());
		this.addTestParameters( testNgTestResult.getParameters(), extensionsMap );
		this.addTestAttributes( testNgTestResult, extensionsMap );
		this.addThrowableExtension( testNgTestResult.getThrowable(), extensionsMap );
		
		diagnostic.put( "extensions", extensionsMap );
	}

	/**
	 * @param attributeNames
	 * @param extensionsMap
	 */
	protected void addTestAttributes( ITestResult testResult,
			LinkedHashMap<String, Object> extensionsMap )
	{
		LinkedHashMap<String, Object> attributesMaps = new LinkedHashMap<String, Object>();
		Set<String> attributeNames = testResult.getAttributeNames();
		for( String attributeName : attributeNames )
		{
			Object attributeValue = testResult.getAttribute( attributeName );
			attributesMaps.put( attributeName, attributeValue );			
		}
		extensionsMap.put( "Attributes", attributesMaps );
	}

	/**
	 * @param parameters
	 * @param extensionsMap
	 */
	protected void addTestParameters( Object[] parameters,
			LinkedHashMap<String, Object> extensionsMap )
	{
		StringBuffer parametersBuffer = new StringBuffer();
		for( int i = 0 ; i < parameters.length ; i++ )
		{
			Object parameter = parameters[i];
			parametersBuffer.append( parameter.toString() );
			if ( i != (parameters.length - 1 ) )
			{
				parametersBuffer.append(", ");
			} 
		}
		extensionsMap.put("Parameters", parametersBuffer.toString());
	}

	/**
	 * @param throwable
	 * @param testResult
	 */
	protected void addThrowableExtension( Throwable throwable,
			LinkedHashMap<String, Object> extensions )
	{
		String exceptionText = ExtUtil.fromThrowableToString(throwable);
		extensions.put("Throwable", exceptionText );
	}

	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult tr) 
	{
		counter+=1;
		TestResult testResult = new TestResult(StatusValues.OK, counter);
		Directive skipDirective = new Directive(DirectiveValues.SKIP, "previous test failed or was skipped.");
		testResult.setDirective( skipDirective );
		this.tapProducer.addTestResult( testResult );
	}
	
	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext testContext) 
	{
		counter = 0;
		final String testName = testContext.getName() + ".tap";
		
		try 
		{
			this.tapProducer.printTo(new File( TAP_OUTPUT_DIRECTORY, testName ));
		}
		catch (IOException e) 
		{
			e.printStackTrace(System.err);
		}
	}
	
}
