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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.collections.Maps;
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
	
	protected final Map<ITestNGMethod, List<ITestResult>> testResultsPerMethod = new LinkedHashMap<ITestNGMethod, List<ITestResult>>();
	
	protected final Map<String, Map<ITestNGMethod, List<ITestResult>>> testResultsPerGroup = new LinkedHashMap<String, Map<ITestNGMethod, List<ITestResult>>>();
	
	/**
	 * TAP Producer.
	 */
	protected TapProducer tapProducer = TapProducerFactory.makeTap13YamlProducer();
	
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
		this.generateTAPPerSuite(xmlSuites, suites, outputDirectory);
		
		this.generateTAPPerGroup(xmlSuites, suites, outputDirectory);
	}
	
	
	/**
	 * Generate a TAP file for every suite tested
	 * 
	 * @param testContext
	 */
	protected void generateTAPPerSuite(
			List<XmlSuite> xmlSuites, 
			List<ISuite> suites, 
			String outputDirectory)
	{
		for ( ISuite suite : suites )
		{
			testSet = new TestSet();
			
			Set<Class<?>> testResultsSet = this.getTestResultsSetPerSuite(suite);
			
			Integer totalTestResults = this.getTotalTestResultsBySuite(testResultsSet);

			testSet.setPlan( new Plan( totalTestResults ) );
			
			for( Class<?> testResultClass : testResultsSet )
			{
				List<ITestResult> testResults = testResultsPerSuite.get( testResultClass );
				
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
	// TAP structure ( group -> class -> methods )
	protected void generateTAPPerGroup(
			List<XmlSuite> xmlSuites, 
			List<ISuite> suites, 
			String outputDirectory)
	{
		for ( ISuite suite : suites )
		{
			Map<String, Collection<ITestNGMethod>> groups = suite.getMethodsByGroups();
			
			if (groups.size() > 0) 
			{
				String[] groupNames = groups.keySet().toArray(new String[groups.size()]);
				Arrays.sort(groupNames);
				
				for (String group : groupNames) 
				{
					if(StringUtils.isNotEmpty(group))
					{
						Collection<ITestNGMethod> methodsByGroup = groups.get(group);
					    
					    StringBuffer methodNames = new StringBuffer();
					    Map<ITestNGMethod, ITestNGMethod> uniqueMethods = Maps.newHashMap();
					    

					    
					    for (ITestNGMethod tm : methodsByGroup) 
					    {
					    	uniqueMethods.put(tm, tm);
					    }
					    
					    for (ITestNGMethod method : uniqueMethods.values()) 
					    {
							List<ITestResult> testResultsForThisMethod = testResultsPerMethod.get( method );
							
							if ( testResultsForThisMethod == null )
							{
								testResultsForThisMethod = new ArrayList<ITestResult>();
								testResultsPerMethod.put(method, testResultsForThisMethod);
							}
							//testResultsForThisMethod.add( testResult );
					    }
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Get a Set of test Results by a given ISuite
	 * 
	 * @param suite
	 * @return
	 */
	protected Set<Class<?>> getTestResultsSetPerSuite(ISuite suite)
	{
		XmlSuite xmlSuite = suite.getXmlSuite();
		
		// Popula o mapa testResultsPerSuite com uma classe para cada suite com seus resultados
		this.generateClasses ( xmlSuite, suite );
		
		return testResultsPerSuite.keySet();
	}
	
	
	/**
	 * Get total results from a test suite
	 * 
	 * @param keySet
	 * @return
	 */
	public Integer getTotalTestResultsBySuite(Set<Class<?>> keySet)
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
	 * Populate a List of ITestResults for every test Class in a test Suite
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
	private static final long serialVersionUID = 1L;

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
