package br.eti.kinoshita.tap4j.ext;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;


public class TestYAMLishUtils 
{
	ITestResult iTestResult;
	
	@BeforeClass
	public void setUp()
	{
		// TBD: generate an iTestResult object with all the information 
		// necessary to test the methods from YAMLishUtils class and TAPUtils class
		iTestResult = new TestResult();
		iTestResult.setAttribute("", "");
	}
	
	@Test
	public void testGetYAMLExcepetionLine()
	{
		String exceptionTrackLine = "br.eti.kinoshita.tap4j.producer.TestTap13YamlProducer.testDumpFailsForMissingPlan(TestTap13YamlProducer.java:178)";
		String strToFind = "br.eti.kinoshita.tap4j.producer.TestTap13YamlProducer.testDumpFailsForMissingPlan(TestTap13YamlProducer.java:";
				
		String lineStr = YAMLishUtils.getLineNumberFromExceptionTraceLine(exceptionTrackLine, strToFind);
		
		Assert.assertTrue ( lineStr!="" );
	}
	
	@Test
	public void testGetMessage()
	{
		ITestResult iTestResult = new TestResult();
		
		String msg = YAMLishUtils.getMessage(iTestResult);
		
		Assert.assertNotNull(msg);
	}
}
