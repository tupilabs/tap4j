package br.eti.kinoshita.tap4j.ext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.producer.TapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducerFactory;

/**
 * Listener implemented to generate a tap file with the testes results for every class and every method tested
 * 
 * @author César Fernandes de Almeida
 *
 */
public class JUnit40TestTapReporter 
extends RunListener 
{
	Result resFinal;
	
	protected List<JUnit40TestData> testMethodsList = null;
	
	/**
	 * TAP Producer.
	 */
	protected TapProducer tapProducer = null;

	 /**
     * Called right before any tests from a specific class are run.
     *
     * @see org.junit.runner.notification.RunListener#testRunStarted(org.junit.runner.Description)
     */
    public void testRunStarted( Description description )
        throws Exception
    {
    	this.tapProducer = TapProducerFactory.makeTap13YamlProducer();
    	
    	this.testMethodsList = new ArrayList<JUnit40TestData>();
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
    	
    	// Generate TAP FILE for each class
    	this.generateTapPerClass( result );
    	
    	// Generate TAP FILE for each method
    	this.generateTapPerMethod( result );
    }

    /**
     * Called when a specific test has been skipped (for whatever reason).
     *
     * @see org.junit.runner.notification.RunListener#testIgnored(org.junit.runner.Description)
     */
    public void testIgnored( Description description )
        throws Exception
    {
    	JUnit40TestData testMethod = new JUnit40TestData(false, false);
    	testMethod.setDescription(description);
    	testMethod.setIgnored( true );
    	testMethodsList.add(testMethod);
    }

    /**
     * Called when a specific test has started.
     *
     * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
     */
    public void testStarted( Description description )
        throws Exception
    {
    	// Guarda as informações desse teste 
    	this.setTestInfo(description);
    }

    /**
     * Called when a specific test has failed.
     *
     * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
     */
    @SuppressWarnings( { "ThrowableResultOfMethodCallIgnored" } )
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
     * Generate tap file for a class
     * 
     * @param data.getDescription()
     */
    protected void generateTapPerClass( Result result )
    {
    	TestSet testSet = new TestSet();
    	testSet.setPlan( new Plan( testMethodsList.size() ) );
    	String className="";
    	for(JUnit40TestData testMethod:testMethodsList)
    	{
			className = JUnit40YAMLishUtils.extractClassName( testMethod.getDescription() );

			TestResult tapTestResult = JUnit40TAPUtils.generateTAPTestResult(testMethod, 1);
			testSet.addTestResult( tapTestResult );
    	}
		
    	File output = new File("./", className+".tap");
		tapProducer.dump(testSet, output);
    }
    
    /**
     * Generate tap file for each method
     * 
     * @param result
     */
    protected void generateTapPerMethod( Result result )
    {
    	for(JUnit40TestData testMethod:testMethodsList)
    	{
			TestResult tapTestResult = JUnit40TAPUtils.generateTAPTestResult(testMethod, 1);
			
        	TestSet testSet = new TestSet();
        	testSet.setPlan( new Plan( 1 ) );
			testSet.addTestResult( tapTestResult );
	    	
			String className  = JUnit40YAMLishUtils.extractClassName( testMethod.getDescription() );
			String methodName = JUnit40YAMLishUtils.extractMethodName( testMethod.getDescription() );
			
			File output = new File( "./", className+"#"+methodName+".tap" );
			tapProducer.dump( testSet, output );
    	}
    }

    /**
     * Set test info
     * 
     * @param description
     */
    protected void setTestInfo( Description description )
    {
    	JUnit40TestData testMethod = new JUnit40TestData(false, false);
    	testMethod.setDescription(description);
    	testMethodsList.add(testMethod);
    }
    
    /**
     * Set failed info 
     * 
     * @param result
     */
    protected void setFailedTestsStatus( Result result )
    {
    	if(result.getFailureCount()>0)
    	{
        	for(Failure f : result.getFailures())
        	{
            	// Change test status to Failed 
            	for(JUnit40TestData testMethod:testMethodsList)
            	{
            		if(testMethod.getDescription().getDisplayName().equals(f.getTestHeader()))
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
