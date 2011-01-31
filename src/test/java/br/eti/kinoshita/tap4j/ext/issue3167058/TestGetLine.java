package br.eti.kinoshita.tap4j.ext.issue3167058;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.ext.YAMLishUtils;

public class TestGetLine 
{
	@Test
	public void testGetYAMLExcepetionLine()
	{
		String exceptionTrackLine = "br.eti.kinoshita.tap4j.producer.TestTap13YamlProducer.testDumpFailsForMissingPlan(TestTap13YamlProducer.java:178)";
		String strToFind = "br.eti.kinoshita.tap4j.producer.TestTap13YamlProducer.testDumpFailsForMissingPlan(TestTap13YamlProducer.java:";
				
		String lineStr = YAMLishUtils.getLineNumberFromExceptionTraceLine(exceptionTrackLine, strToFind);
		
		Assert.assertTrue ( lineStr!="" );
	}
}
