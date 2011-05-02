/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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
package org.tap4j.ext.testng;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ResultMap;

/**
 * Utility class with methods to support TAP generation with TestNG.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @author Cesar Fernandes de Almeida
 * @since 1.1
 */
public final class TestNGTAPUtils
{
	
	/**
	 * Hidden constructor.
	 */
	private TestNGTAPUtils()
	{
		super();
	}
	
	/**
	 * A constant that defines a comparator for TestNG TestResults. This  
	 * comparator returns the TestNG TestResults ordered by execution 
	 * date. For example, if the test A ran at 08:00 and the test B ran at 
	 * 07:00, it will say that the correct order for these elements 
	 * is [B, A].
	 */
	public static final Comparator<ITestResult> EXECUTION_DATE_COMPARATOR =
		new ExecutionDateCompator();
	
	/**
	 * Generates a TAP TestResult from a given TestNG TestResult. 
	 * 
	 * @param testResult TestNG Test Result
	 * @param number TAP Test Number
	 * @return TAP TestResult
	 */
	public static TestResult generateTAPTestResult( ITestResult testResult, Integer number )
	{
		final TestResult tapTestResult = new TestResult();
		
		String testResultDescription = generateTAPTestResultDescription( testResult );
		tapTestResult.setDescription( testResultDescription );
		
		TestNGTAPUtils.setTapTestResultStatus( tapTestResult, testResult.getStatus() );
		
		TestNGTAPUtils.createTestNGYAMLishData( tapTestResult, testResult );
		
		return tapTestResult;
	}
	
	/**
	 * Generates a TAP TestResult description with full qualified class name 
	 * concatenated with the character '#' and the test method.  
	 * 
	 * @param testResult TestNG TestResult
	 * @return Name of TAP Test Result
	 */
	public static String generateTAPTestResultDescription( ITestResult testResult ) 
	{
		StringBuilder description = new StringBuilder();
		description.append( "- " ); // An extra space is added before the description by the TAP Representer
		description.append( testResult.getTestClass().getName() );
		description.append( '#' );
		description.append( testResult.getMethod().getMethodName() );
		return description.toString();
	}
	
	/**
	 * Sets the StatusValue into a TAP TestResult. In cases where the StatusValue 
	 * is equal SKIP a Directive is also created and added to the TAP Test Result.
	 * 
	 * @param tapTestResult TAP TestResult
	 * @param status TestNG Test Status (Success, Skip, or any other that is 
	 * treated as Failed in TAP)
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
	 * <p>
	 * Inserts TestNG YAMLish diagnostic information into a TAP TestResult.
	 * </p>
	 * 
	 * <p>
	 * For more about TAP YAMLish diagnostic read this   
	 * <a href="http://testanything.org/wiki/index.php/YAMLish">Wiki</a>.
	 * </p>
	 * 
	 * @param testResult TAP TestResult
	 * @param testNgTestResult TestNG TestResult
	 */
	public static void createTestNGYAMLishData( 
			TestResult testResult, 
			ITestResult testNgTestResult )
	{
		final Map<String, Object> yamlish = testResult.getDiagnostic();
		
		// Root namespace
		
		createYAMLishMessage( yamlish, testNgTestResult );
		createYAMLishSeverity( yamlish, testNgTestResult ); 
		createYAMLishSource( yamlish, testNgTestResult );
		createYAMLishDatetime( yamlish, testNgTestResult );
		createYAMLishFile( yamlish, testNgTestResult );
		createYAMLishLine( yamlish, testNgTestResult );
		createYAMLishName( yamlish, testNgTestResult );
		createYAMLishExtensions( yamlish, testNgTestResult );
		createYAMLishActualAndExpected( yamlish, testNgTestResult );
		createYAMLishDisplay( yamlish, testNgTestResult );
		createYAMLishDump( yamlish, testNgTestResult );
		createYAMLishError( yamlish, testNgTestResult );
		createYAMLishBacktrace( yamlish, testNgTestResult );
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishMessage(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String message = TestNGYAMLishUtils.getMessage(testNgTestResult);
		yamlish.put( "message", message );
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishSeverity(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String severity = TestNGYAMLishUtils.getSeverity( testNgTestResult );
		yamlish.put( "severity", severity );
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishSource(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String source = TestNGYAMLishUtils.getSource( testNgTestResult );			
		yamlish.put( "source", source );
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishDatetime(
		Map<String, Object> yamlish, 
		ITestResult testNgTestResult) 
	{
		String datetime = TestNGYAMLishUtils.getDatetime(testNgTestResult);
		yamlish.put( "datetime", datetime );
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishFile(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String file = TestNGYAMLishUtils.getFile( testNgTestResult );
		yamlish.put("file", file);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishLine(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String line = TestNGYAMLishUtils.getLine( testNgTestResult );
		yamlish.put("line", line);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishName(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String name = TestNGYAMLishUtils.getName(testNgTestResult);
		yamlish.put( "name", name );
	}
	
	/**
	 * Creates YAMLish diagnostic extensions entry data.
	 * 
	 * @param yamlish YAMLish Map
	 * @param testNgTestResult TestNG TestResult
	 */
	public static void createYAMLishExtensions(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		Object extensions = TestNGYAMLishUtils.getExtensions( testNgTestResult );
		yamlish.put("extensions", extensions);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishActualAndExpected(
			Map<String, Object> yamlish, 
			ITestResult testNgTestResult) 
	{
		String expected = TestNGYAMLishUtils.getExpected( testNgTestResult );
		String actual = TestNGYAMLishUtils.getActual(testNgTestResult);

		if ( expected == null )
		{
			expected = "~";
		}
		
		if ( actual == null )
		{
			actual = "~";
		}
		
		yamlish.put("got", actual);
		yamlish.put("expected", expected);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishDisplay(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{

		String display = TestNGYAMLishUtils.getDisplay( testNgTestResult );
		yamlish.put("display", display);		
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishDump(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		Object dump = TestNGYAMLishUtils.getDump( testNgTestResult );
		yamlish.put("dump", dump);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishError(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		String error = TestNGYAMLishUtils.getError( testNgTestResult );
		yamlish.put("error", error);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishBacktrace(
			Map<String, Object> yamlish,
			ITestResult testNgTestResult) 
	{
		Object backtrace = TestNGYAMLishUtils.getBacktrace( testNgTestResult );
		yamlish.put( "backtrace", backtrace );
	}
	
	/**
	 * Return an ordered list of TestNG TestResult from a given TestNG Test 
	 * Context.
	 * 
	 * @param testContext TestNG Test Context
	 * @return Ordered list of TestNG TestResults
	 */
	public static List<ITestResult> getTestNGResultsOrderedByExecutionDate(ITestContext testContext)
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
		
		ITestNGMethod[] allMethodsInCtx = testContext.getAllTestMethods();
		for (int i = 0; i < allMethodsInCtx.length; i++)
		{
			ITestNGMethod methodInCtx = allMethodsInCtx [ i ];
			
			Collection<ITestNGMethod> allMethodsFound = total.getAllMethods();
			boolean exists = false;
			for( ITestNGMethod methodFound : allMethodsFound )
			{
				if ( methodInCtx.getTestClass().getName().equals(methodFound.getTestClass().getName()))
				{
					if ( methodInCtx.getMethod().getName().equals(methodFound.getMethod().getName()))
					{
						exists = true;
					}
				}
			}
			if ( ! exists )
			{
				ITestResult skippedTestResult = 
					new org.testng.internal.TestResult(methodInCtx.getTestClass(), methodInCtx.getInstances(), methodInCtx, null, testContext.getStartDate().getTime(), testContext.getEndDate().getTime());
				skippedTestResult.setStatus(ITestResult.SKIP);
				total.addResult(skippedTestResult, methodInCtx);
			}
		}
		
		List<ITestResult> testNGTestResults = new ArrayList<ITestResult>(total.getAllResults() );
		Collections.sort(testNGTestResults, EXECUTION_DATE_COMPARATOR);
		
		return testNGTestResults;
	}
	
	/**
	 * Return an ordered list of TestNG TestResult from a given TestNG Test 
	 * Context.
	 * 
	 * @param total TestNG Result Map
	 * @return Ordered list of TestNG TestResults
	 */
	public static List<ITestResult> getTestNGResultsOrderedByExecutionDate(ResultMap total)
	{
		List<ITestResult> testNGTestResults = new ArrayList<ITestResult>(total.getAllResults() );
		Collections.sort(testNGTestResults, EXECUTION_DATE_COMPARATOR);
		
		return testNGTestResults;
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
	 * Fills the TestNG Attributes from the context into the TestNG Test Result.
	 * 
	 * @param tr
	 * @param ctx
	 */
	public static void fillAttributes( ITestResult tr, ITestContext ctx )
	{
		final Set<String> attrsNames = ctx.getAttributeNames();
		for( String attr : attrsNames )
		{
			Object o = ctx.getAttribute(attr);
			if ( o instanceof TAPAttribute )
			{
				TAPAttribute tapAttr = (TAPAttribute)o;
				ITestNGMethod testNGMethod = tr.getMethod();
				Method method = testNGMethod.getMethod();
				if ( method == tapAttr.getMethod() )
				{
					tr.setAttribute(attr, tapAttr.getValue());
				}
			}
		}
	}
	
}
