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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.producer.TapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducerFactory;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/01/2011
 */
public class SuiteTAPReporter 
implements IReporter
{
	protected final Map<Class<?>, List<ITestResult>> testResultsPerSuite = new LinkedHashMap<Class<?>, List<ITestResult>>();
	
	/**
	 * TAP Producer.
	 */
	protected TapProducer tapProducer = TapProducerFactory.makeTap13Producer();
	
	/**
	 * TAP Test Set
	 */
	TestSet testSet;
	
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
			
			// Popula o mapa testResultsPerSuite com uma classe para cada suite com seus resultados
			this.generateClasses ( xmlSuite, suite );
			
			Set<Class<?>> keySet = testResultsPerSuite.keySet();
			
			testSet = new TestSet();
			
			Integer totalTestResults = this.getTotalResultsBySuite(keySet);

			testSet.setPlan( new Plan( totalTestResults ) );
			
			for( Class<?> clazz : keySet )
			{
				List<ITestResult> testResults = testResultsPerSuite.get( clazz );
				for ( ITestResult testResult : testResults )
				{
					TestResult tapTestResult = TAPUtils.generateTAPTestResult( testResult, testSet.getNumberOfTestResults()+1 );
					testSet.addTestResult( tapTestResult );
				}
			}
			
			File output = new File(outputDirectory, "SuiteTest-"+suite.getName()+".tap");
			tapProducer.dump(testSet, output);
		}
	}
	
	
	/**
	 * Generate a TAP file for every group tested
	 * 
	 * @param testContext
	 */
	// TBD: Method to generate TAP file by test Group
	protected void generateTAPPerGroup(ITestContext testContext)
	{
	}	
	
	
	/**
	 * Get total results from a test suite
	 * 
	 * @param keySet
	 * @return
	 */
	public Integer getTotalResultsBySuite(Set<Class<?>> keySet)
	{
		Integer totalTestResults = 0;
		for( Class<?> clazz : keySet )
		{
			List<ITestResult> testResults = testResultsPerSuite.get( clazz );
			totalTestResults += testResults.size();
		}
		return totalTestResults;
	}
	
	
	/**
	 * Populate a List of ITestResults for every class in a Suite
	 * 
	 * @param xmlSuite
	 * @param suite
	 */
	public void generateClasses( XmlSuite xmlSuite, ISuite suite )
	{
		if ( suite.getResults().size() > 0 )
		{
			for ( ISuiteResult suiteResult : suite.getResults().values() )
			{
				List<ITestResult> testResults = TAPUtils.getTestNGResults(suiteResult.getTestContext());
				
				for ( ITestResult testResult : testResults )
				{
					Class<?> clazz = testResult.getMethod().getRealClass();
					List<ITestResult> testResultsForThisClass = testResultsPerSuite.get( clazz );
					
					if ( testResultsForThisClass == null )
					{
						testResultsForThisClass = new ArrayList<ITestResult>();
						testResultsPerSuite.put(clazz, testResultsForThisClass);
					}
					testResultsForThisClass.add( testResult );
				}
			}
		}
	}

}


class ExecutionDateCompator 
implements Comparator<ITestResult>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5496973354025848177L;

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
