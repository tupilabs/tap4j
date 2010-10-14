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
package br.eti.kinoshita.tap4j;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.consumer.TapParserException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTapParserException 
extends Assert
{
	
	private TapParserException exception;
	
	@Test
	public void testTapParserException1()
	{
		exception = new TapParserException();
		
		assertNotNull( exception );
	}
	
	@Test
	public void testTapParserException2()
	{
		exception = new TapParserException("Error parsing document");
		
		assertNotNull( exception );
		
		assertEquals( exception.getMessage(), "Error parsing document");
	}
	
	@Test
	public void testTapParserException3()
	{
		exception = new TapParserException(new NullPointerException("Null TAP Stream"));
		
		assertNotNull( exception );
		
		assertTrue( exception.getCause() instanceof NullPointerException );
	}
	
	@Test
	public void testTapParserException4()
	{
		exception = new TapParserException("Null", new NullPointerException());
		
		assertNotNull( exception );
		
		assertEquals( exception.getMessage(), "Null");
		
		assertTrue( exception.getCause() instanceof NullPointerException );
	}

}
