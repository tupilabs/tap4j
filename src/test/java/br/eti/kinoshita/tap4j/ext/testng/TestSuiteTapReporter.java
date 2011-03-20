package br.eti.kinoshita.tap4j.ext.testng;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * 
 * @author Cesar Fernandes de Almeida
 *
 * @since 18/03/2011
 *
 */
@Listeners({ br.eti.kinoshita.tap4j.ext.testng.SuiteTAPReporter.class })
public class TestSuiteTapReporter 
{
	@Test(groups="GropoTeste1")
	public void teste1() 
	{
		Assert.assertNull(null);
	}
	
	@Test(groups="GropoTeste1")
	public void teste2() 
	{
		Assert.assertTrue(true);
	}
	
	@Test(groups="GropoTeste2")
	public void teste3() 
	{
		Assert.assertFalse(false);
	}
	
	@Test(groups="GropoTeste2")
	public void teste4() 
	{
		Assert.assertEquals("", "");
	}
}
