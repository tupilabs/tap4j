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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.ResultMap;

import br.eti.kinoshita.tap4j.model.Directive;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.util.DirectiveValues;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/01/2011
 */
public class TAPUtils
{
	
	public static final Comparator<ITestResult> EXECUTION_DATE_COMPARATOR =
		new ExecutionDateCompator();
	
	/**
	 * Adds Throwable diagnostic information to a TAP Test Result.
	 * 
	 * @param tapTestResult TAP Test Result.
	 * @param testNgTestResult TestNG Test Result.
	 */
	public static void addTestNGThrowableDiagnostic(
		TestResult tapTestResult, 
		ITestResult testNgTestResult )
	{
		final Throwable t = testNgTestResult.getThrowable();
		if ( t != null )
		{
			final StringWriter sw = new StringWriter();
			t.printStackTrace( new PrintWriter(sw) );
			
			Map<String, Object> testNGMap = getTestNGDiagnosticMap(tapTestResult);
			testNGMap.put("Exception", sw.toString());
		}
	}
	
	/**
	 * 
	 * @param testResult
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTestNGDiagnosticMap( TestResult testResult )
	{
		LinkedHashMap<String, Object> testNGMap = null;		
		if ( testResult.getDiagnostic() == null )
		{
			testResult.setDiagnostic(new LinkedHashMap<String, Object>());
		} 
		testNGMap = (LinkedHashMap<String, Object>) testResult.getDiagnostic().get("testng");
		if ( testNGMap == null )
		{
			testNGMap = 
				new LinkedHashMap<String, Object>();
			testResult.getDiagnostic().put("Testng", testNGMap);
		}
		return testNGMap;
	}
	
	/**
	 * 
	 * @param testResult
	 * @param number
	 * @return
	 */
	public static TestResult generateTAPTestResult( ITestResult testResult, int number )
	{
		TestResult tapTestResult = new TestResult(StatusValues.NOT_OK, number);
		tapTestResult.setDescription( "- " + testResult.getName() );
		
		TAPUtils.setTapTestResultStatus( tapTestResult, testResult.getStatus() );
		
		final Map<String, Object> testNGMap = 
			TAPUtils.getTestNGDiagnosticMap(tapTestResult);
		testNGMap.put("Class", testResult.getTestClass().getName());
		
		addTestNGThrowableDiagnostic(tapTestResult, testResult);
		
		return tapTestResult;
	}
	
	/**
	 * 
	 * @param tapTestResult
	 * @param status
	 */
	public static void setTapTestResultStatus( TestResult tapTestResult, int status )
	{
		switch ( status )
		{
		case ITestResult.SUCCESS:
			tapTestResult.setStatus(StatusValues.OK);
			break;
		case ITestResult.SKIP:
			tapTestResult.setStatus(StatusValues.NOT_OK);
			Directive skip = new Directive(DirectiveValues.SKIP, "TestNG test was skipped");
			tapTestResult.setDirective( skip );
			break;
		default:
			tapTestResult.setStatus(StatusValues.NOT_OK);
			break;
		}
	}
	
	
	/**
	 * Adds all ITestResult's inside the map object inside the total one.
	 * 
	 * @param total ResultMap that holds the total of IResultMap's.
	 * @param map An IResultMap object.
	 */
	public static void addAll( ResultMap total, IResultMap map )
	{
		for ( ITestResult testResult : map.getAllResults() )
		{
			total.addResult( testResult, testResult.getMethod() );
		}
	}
	
	
	/**
	 * Return an ordered list of ITestResults
	 * 
	 * @param testContext
	 * @return
	 */
	public static List<ITestResult> getTestNGResults(ITestContext testContext)
	{
		Map<String, IResultMap> results= new LinkedHashMap<String, IResultMap>();
		results.put("passed", testContext.getPassedTests());
		results.put("failed", testContext.getFailedTests());
		results.put("failedBut", testContext.getFailedButWithinSuccessPercentageTests());
		results.put("skipped", testContext.getSkippedTests());
		
		ResultMap total = new ResultMap();
		
		addAll( total, results.get("passed") );
		addAll( total, results.get("failed") );
		addAll( total, results.get("failedBut") );
		addAll( total, results.get("skipped") );
		
		List<ITestResult> testNGTestResults = new ArrayList<ITestResult>(total.getAllResults() );
		Collections.sort(testNGTestResults, EXECUTION_DATE_COMPARATOR);
		
		return testNGTestResults;
	}

}
