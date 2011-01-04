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

import java.io.IOException;

import junit.framework.Assert;

import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
@Listeners(value={TestTAPReporter.class})
public class TestNGIntegration 
extends Assert
{

	@Test(singleThreaded=true)
	public void simpleTestOk()
	{
		assertTrue( 1 > 0 );
	}
	
	@SuppressWarnings("unused")
	@Parameters({"name", "age"})
	@Test(singleThreaded=true,successPercentage=0,dependsOnMethods={"simpleTestOk"}, expectedExceptions=IOException.class, description="A test that purposefully fails.")
	public void simpleTestFail(@Optional("Johnson") String name, @Optional("19") Integer age) 
	throws Exception
	{
		if ( 1 > 0 )
		{
			throw new Exception("Error");	
		}
		fail("no reason");
	}
	
	@Test(dependsOnMethods={"simpleTestFail"}, alwaysRun=false)
	public void simpleTestSkip()
	{
		
	}
	
	@Test
	public void anotherThatFails()
	{
		assertEquals(13, System.currentTimeMillis());		
	}
	
}
