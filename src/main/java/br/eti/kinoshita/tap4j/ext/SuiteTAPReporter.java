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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.ResultMap;
import org.testng.xml.XmlSuite;

import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.producer.DefaultTapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducer;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/01/2011
 */
public class SuiteTAPReporter 
implements IReporter
{
	
	final Map<Class<?>, List<ITestResult>> testResultsPerClass = new LinkedHashMap<Class<?>, List<ITestResult>>();
	
	protected TapProducer tapProducer;
	
	/* (non-Javadoc)
	 * @see org.testng.IReporter#generateReport(java.util.List, java.util.List, java.lang.String)
	 */
	public void generateReport( 
		List<XmlSuite> xmlSuites, 
		List<ISuite> suites,
		String outputDirectory )
	{
		for ( ISuite suite : suites )
		{
			XmlSuite xmlSuite = suite.getXmlSuite();
			this.generateClasses ( xmlSuite, suite );
		}
		
		Set<Class<?>> keySet = testResultsPerClass.keySet();
		
		for( Class<?> clazz : keySet )
		{
			tapProducer = new DefaultTapProducer();
			
			List<ITestResult> testResults = testResultsPerClass.get( clazz );
			tapProducer.setPlan( new Plan(testResults.size()) );
			
			for ( ITestResult testResult : testResults )
			{
				TestResult tapTestResult = TAPUtils.generateTAPTestResult( testResult, tapProducer.getNumberOfTestResults()+1 );
				tapProducer.addTestResult( tapTestResult );
			}
			
			File output = new File(outputDirectory, ""+clazz.getName()+".tap");
			TAPUtils.writeTAPReport(tapProducer, output);
		}
	}
	
	public void generateClasses( XmlSuite xmlSuite, ISuite suite )
	{
		if ( suite.getResults().size() > 0 )
		{
			for ( ISuiteResult suiteResult : suite.getResults().values() )
			{
				ITestContext testContext = suiteResult.getTestContext();
				
				IResultMap passed = testContext.getPassedTests();
				IResultMap failed = testContext.getFailedTests();
				IResultMap failedBut = testContext.getFailedButWithinSuccessPercentageTests();
				IResultMap skipped = testContext.getSkippedTests();
				
				ResultMap total = new ResultMap();
				
				TAPUtils.addAll( total, passed );
				TAPUtils.addAll( total, failed );
				TAPUtils.addAll( total, failedBut );
				TAPUtils.addAll( total, skipped );
				
				List<ITestResult> testResults = new ArrayList<ITestResult>(total.getAllResults() );
				Collections.sort(testResults, TAPUtils.EXECUTION_DATE_COMPARATOR);
				
				for ( ITestResult testResult : testResults )
				{
					Class<?> clazz = testResult.getMethod().getRealClass();
					List<ITestResult> testResultsForThisClass = 
						testResultsPerClass.get( clazz );
					if ( testResultsForThisClass == null )
					{
						testResultsForThisClass = new ArrayList<ITestResult>();
						testResultsPerClass.put(clazz, testResultsForThisClass);
					}
					testResultsForThisClass.add( testResult );
				}
			}
		}
	}

}

class ExecutionDateCompator 
implements Comparator<ITestResult>
{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare( ITestResult o1, ITestResult o2 )
	{
		if ( o1.getStartMillis() > o2.getStartMillis() )
		{
			return 1;
		}
		return -1;
	}

}
