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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import br.eti.kinoshita.tap4j.model.Directive;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
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

	protected TapProducer tapProducer = new DefaultTapProducer();
	
	protected static int counter = 0;
	
	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext testContext)
	{
		this.tapProducer.setHeader(new Header(13));		
		int numberOfTests = testContext.getAllTestMethods().length;
		this.tapProducer.setPlan(new Plan(numberOfTests));
	}
	
	/* (non-Javadoc)
	 * @see org.testng.TestListenerAdapter#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult tr) 
	{
		counter+=1;
		TestResult testResult = new TestResult(StatusValues.OK, counter);
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
				
		if ( tr.getThrowable() != null )
		{
			LinkedHashMap<String, Object> diagnostic = new LinkedHashMap<String, Object>();
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter( sw, true );
			tr.getThrowable().printStackTrace( writer );
			writer.flush();
			sw.flush();
			String exceptionText = sw.toString();
			exceptionText = exceptionText.replaceAll("(\r|\n|\t)", "");
			diagnostic.put("Throwable", exceptionText );
			testResult.setDiagnostic(diagnostic);
		}
		
		this.tapProducer.addTestResult( testResult );
		
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
		String testName = testContext.getName();
		
		try 
		{
			this.tapProducer.printTo(new File( testName ));
		}
		catch (IOException e) 
		{
			e.printStackTrace(System.err);
		}
	}
	
}
