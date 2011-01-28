package br.eti.kinoshita.tap4j.ext;

import java.util.Map;

import org.testng.IResultMap;
import org.testng.ITestResult;
import org.testng.internal.ResultMap;

import br.eti.kinoshita.tap4j.model.Directive;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.util.DirectiveValues;
import br.eti.kinoshita.tap4j.util.StatusValues;

class JUnit40TAPUtils
{
	private JUnit40TAPUtils()
	{
		super();
	}
	
	/**
	 * Generate TAP test result
	 * 
	 * @param testMethod
	 * @param number
	 * @return
	 */
	public static TestResult generateTAPTestResult( JUnit40TestData testMethod, Integer number )
	{
		final TestResult tapTestResult = new TestResult();
		
		String testResultDescription = generateTAPTestResultDescription( testMethod );
		tapTestResult.setDescription( testResultDescription );
		
		setTapTestStatus( tapTestResult, testMethod );
		
		createTestNGYAMLishData( tapTestResult, testMethod );
		
		return tapTestResult;
	}
	
    /**
     * Get the tap test description
     * 
     * @param testMethod
     * @return the tap test description 
     */
	public static String generateTAPTestResultDescription( JUnit40TestData testMethod )
    {
    	StringBuilder description = new StringBuilder();
    	
    	// An extra space is added before the description by the TAP Representer
    	description.append( "- " ); 
    	description.append( JUnit40YAMLishUtils.extractClassName( testMethod.getDescription() ) );
    	description.append( '#' );
    	description.append( JUnit40YAMLishUtils.extractMethodName( testMethod.getDescription() ) );
    	
    	return description.toString();
    }
	
	/**
     * Set the tap status
	 * 
	 * @param tapTestResult
	 * @param testMethod
	 */
    public static void setTapTestStatus( TestResult tapTestResult, JUnit40TestData testMethod )
    {
    	if(testMethod.isIgnored())
    	{
			tapTestResult.setStatus(StatusValues.NOT_OK);
			Directive skip = new Directive(DirectiveValues.SKIP, "JUnit test was skipped");
			tapTestResult.setDirective( skip );
    	}
    	else if(testMethod.isFailed())
		{
			tapTestResult.setStatus(StatusValues.NOT_OK);
		}
		else
		{
			tapTestResult.setStatus(StatusValues.OK);
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
	 * @param JUnit40Test testMethod with JUnit test info
	 */
	public static void createTestNGYAMLishData( 
			TestResult testResult, 
			JUnit40TestData testMethod )
	{
		final Map<String, Object> yamlish = testResult.getDiagnostic();
		
		// Root namespace
		
		createYAMLishMessage( yamlish, testMethod );
		createYAMLishSeverity( yamlish, testMethod ); 
		createYAMLishSource( yamlish, testMethod );
		createYAMLishDatetime( yamlish );
		createYAMLishFile( yamlish, testMethod );
		createYAMLishLine( yamlish, testMethod );
		createYAMLishName( yamlish, testMethod );
		createYAMLishError( yamlish, testMethod );
		createYAMLishBacktrace( yamlish, testMethod );
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishMessage(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String message = JUnit40YAMLishUtils.getMessage( testMethod );
		yamlish.put( "message", message );
	}
	
	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishSeverity(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String severity = JUnit40YAMLishUtils.getSeverity( testMethod );
		yamlish.put( "severity", severity );
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishSource(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String methodName = JUnit40YAMLishUtils.extractMethodName(testMethod.getDescription());
		String className = JUnit40YAMLishUtils.extractClassName(testMethod.getDescription());
		
		String source = JUnit40YAMLishUtils.getSource(methodName, className);			
		yamlish.put( "source", source );
	}
	
	/**
	 * 
	 * @param yamlish
	 */
	public static void createYAMLishDatetime(Map<String, Object> yamlish) 
	{
		String datetime = JUnit40YAMLishUtils.getDatetime();
		yamlish.put( "datetime", datetime );
	}
	
	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishFile(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String file = JUnit40YAMLishUtils.getFile( testMethod );
		yamlish.put("file", file);
	}
	
	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishLine(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String line = JUnit40YAMLishUtils.getLine( testMethod );
		yamlish.put("line", line);
	}
	
	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishName(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String name = JUnit40YAMLishUtils.getName( testMethod );
		yamlish.put( "name", name );
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishError(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		String error = JUnit40YAMLishUtils.getError( testMethod );
		yamlish.put("error", error);
	}
	
	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishBacktrace(
			Map<String, Object> yamlish,
			JUnit40TestData testMethod) 
	{
		Object backtrace = JUnit40YAMLishUtils.getBacktrace( testMethod );
		yamlish.put( "backtrace", backtrace );
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
}

