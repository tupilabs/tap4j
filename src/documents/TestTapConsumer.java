/**
 * 
 */
package br.eti.kinoshita.tap4j;

import java.io.File;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class TestTapConsumer {

	private TapConsumer consumer;
	private File tapFile;
	
	@BeforeMethod
	public void setUp() 
	throws Exception
	{
		consumer = new DefaultTapConsumer();
		tapFile = new File("c:\\tap1.t");
	}
	
	@Test
	public void testParser() 
	throws Exception
	{
		consumer.parseFile( tapFile );
		
		System.out.println(consumer.getNumberOfTestResults());
		
		for ( int i = 0 ; i < consumer.getNumberOfTestResults();++i)
		{
			System.out.println( consumer.getTestResult(i) );
		}
		
		System.out.println(consumer.getTestPlan());
	}
	
}
