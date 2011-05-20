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
package org.tap4j.ext.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.TapProducer;
import org.tap4j.producer.TapProducerFactory;

/**
 * Listener implemented to generate a tap file with the testes results for every class, suite and method tested
 * 
 * @author Cesar Fernandes de Almeida
 * @since 1.4.3
 */
public class JUnitTestTapReporter 
extends RunListener 
{
	protected List<JUnitTestData> testMethodsList = null;
	
	/**
	 * TAP Producer.
	 */
	protected TapProducer tapProducer = null;
	
	protected boolean isSuite = false;

	 /**
     * Called right before any tests from a specific class are run.
     *
     * @see org.junit.runner.notification.RunListener#testRunStarted(org.junit.runner.Description)
     */
    public void testRunStarted( Description description )
        throws Exception
    {
    	this.tapProducer = TapProducerFactory.makeTap13YamlProducer();
    	
    	this.testMethodsList = new ArrayList<JUnitTestData>();
    	
    	this.isSuite = description.isSuite();
    }

    /**
     * Called right after all tests from a specific class are run.
     *
     * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
     */
    public void testRunFinished( Result result )
        throws Exception
    {    
    	// Set Failed Status on Tests List 
    	this.setFailedTestsStatus( result );
    	
    	// Generate TAP FILE for each method
    	this.generateTapPerMethod( result );
    	
    	// Generate TAP FILE for each class
    	this.generateTapPerClass( result );
    	
    	// Generate TAP FILE for each suite
    	if( this.isSuite )
    	{
        	this.generateTapPerSuite( result );
    	}
    }

    /**
     * Called when a specific test has been skipped (for whatever reason).
     *
     * @see org.junit.runner.notification.RunListener#testIgnored(org.junit.runner.Description)
     */
    public void testIgnored( Description description )
        throws Exception
    {
    	JUnitTestData testMethod = new JUnitTestData( false, false );
    	testMethod.setDescription( description );
    	testMethod.setIgnored( true );
    	testMethodsList.add( testMethod );
    }

    /**
     * Called when a specific test has started.
     *
     * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
     */
    public void testStarted( Description description )
        throws Exception
    {
    	this.setTestInfo( description );
    }

    /**
     * Called when a specific test has failed.
     *
     * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
     */
    public void testFailure( Failure failure )
        throws Exception
    {
    }
    
    /**
     * Called after a specific test has finished.
     *
     * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
     */
    public void testFinished( Description description )
        throws Exception
    {
    }
    
    /**
     * Generate tap file for each method
     * 
     * @param result
     */
    protected void generateTapPerMethod( Result result )
    {
    	for( JUnitTestData testMethod:testMethodsList )
    	{
			TestResult tapTestResult = JUnitTAPUtils.generateTAPTestResult(testMethod, 1);
			
        	TestSet testSet = new TestSet();
        	testSet.setPlan( new Plan( 1 ) );
			testSet.addTestResult( tapTestResult );
	    	
			String className  = JUnitYAMLishUtils.extractClassName( testMethod.getDescription() );
			String methodName = JUnitYAMLishUtils.extractMethodName( testMethod.getDescription() );
			
			File output = new File( "./", className+"#"+methodName+".tap" );
			tapProducer.dump( testSet, output );
    	}
    }
    
    /**
     * Generate tap file for a class
     * 
     * @param result
     */
    protected void generateTapPerClass( Result result )
    {
    	TestSet testSet = new TestSet();
    	File output;
    	Integer methodsSizeList = 0;
    	
    	String className="";
    	String lastClassName="";
    	
    	for( JUnitTestData testMethod:testMethodsList )
    	{
			className = JUnitYAMLishUtils.extractClassName( testMethod.getDescription() );
			
			if( StringUtils.isNotBlank( lastClassName ) && !lastClassName.equals( className ) )
			{
		    	testSet.setPlan( new Plan( methodsSizeList ) );
		    	output = new File( "./", lastClassName+".tap" );
				tapProducer.dump( testSet, output );
				
				testSet = new TestSet();
				methodsSizeList = 0;
			}
			TestResult tapTestResult = JUnitTAPUtils.generateTAPTestResult( testMethod, 1 );
			testSet.addTestResult( tapTestResult );
			methodsSizeList++;
			
			lastClassName = className;
    	}
		
    	testSet.setPlan( new Plan( methodsSizeList ) );
    	output = new File( "./", className+".tap" );
		tapProducer.dump( testSet, output );
    }
    
    /**
     * Generate tap file for per suite
     * 
     * @param result
     */
    protected void generateTapPerSuite( Result result )
    {
    	TestSet testSet = new TestSet();
    	testSet.setPlan( new Plan( testMethodsList.size() ) );
    	String className="";
    	
    	for( JUnitTestData testMethod:testMethodsList )
    	{
			className = JUnitYAMLishUtils.extractClassName( testMethod.getDescription() );

			TestResult tapTestResult = JUnitTAPUtils.generateTAPTestResult(testMethod, 1);
			testSet.addTestResult( tapTestResult );
    	}
		
    	File output = new File( "./", className+"-SUITE.tap" );
		tapProducer.dump( testSet, output );
    }

    /**
     * Set test info
     * 
     * @param description
     */
    protected void setTestInfo( Description description )
    {
    	JUnitTestData testMethod = new JUnitTestData( false, false );
    	testMethod.setDescription( description );
    	testMethodsList.add( testMethod );
    }
    
    /**
     * Set failed info 
     * 
     * @param result
     */
    protected void setFailedTestsStatus( Result result )
    {
    	if( result.getFailureCount() > 0 )
    	{
        	for( Failure f : result.getFailures() )
        	{
            	// Change test status to Failed 
            	for( JUnitTestData testMethod:testMethodsList )
            	{
            		if( testMethod.getDescription().getDisplayName().equals( f.getTestHeader() ) )
            		{
            			testMethod.setFailed( true );
            			testMethod.setFailMessage( f.getMessage() );
            			testMethod.setFailException( f.getException() );
            			testMethod.setFailTrace( f.getTrace() );
            		}
            	}
        	}
    	}
    }
}
