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
package br.eti.kinoshita.tap4j.model;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.util.DirectiveValues;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * Tests Test Results and Directives/Status.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTAPTestResult 
extends Assert
{

	protected TestResult okTestResult = null;
	protected TestResult notOkTestResult = null;
	protected TestResult okTestResultSkip = null;
	
	@BeforeTest
	public void setUp()
	{
		okTestResult = new TestResult( StatusValues.OK, 1 );
		okTestResult.setDescription("- First test");
		
		notOkTestResult = new TestResult( StatusValues.NOT_OK, 2 );
		
		okTestResultSkip = new TestResult( StatusValues.NOT_OK, 3 );

		Directive skipDirective = new Directive( DirectiveValues.SKIP, "Skip it until next release of the produce." );
		okTestResultSkip.setDirective( skipDirective );

		Comment comment = new Comment( "This status is set to true in another method." );
		okTestResultSkip.setComment( comment );
		
	}
	
	@Test
	public void testOkTestResult()
	{
		assertNotNull( okTestResult );
		
		assertTrue( okTestResult.getTestNumber() > 0 );
		
		assertEquals( okTestResult.getStatus(), StatusValues.OK );
		
		assertNull( okTestResult.getDirective() );
		
		String toStringResult = okTestResult.toString();
		
		final String expectedValue = "ok " +okTestResult.getTestNumber()+ " " + okTestResult.getDescription();
		
		assertEquals( toStringResult, expectedValue );
	}
	
	@Test
	public void testNotOkTestResult()
	{
		assertNotNull ( notOkTestResult );
		
		assertTrue ( notOkTestResult.getTestNumber() > 0 );
		
		assertEquals( notOkTestResult.getStatus(), StatusValues.NOT_OK );
		
		assertNull ( notOkTestResult.getDirective() );
		
		String toStringResult = notOkTestResult.toString();
		
		final String expectedValue = "not ok " + notOkTestResult.getTestNumber();
		
		assertEquals( toStringResult, expectedValue );
	}
	
	@Test
	public void testOkTestResultSkip()
	{
		assertNotNull( okTestResultSkip );
		
		assertTrue( okTestResultSkip.getTestNumber() > 0 );
		
		okTestResultSkip.setStatus( StatusValues.OK );
		
		assertEquals( okTestResultSkip.getStatus(), StatusValues.OK );
		
		assertNotNull( okTestResultSkip.getDirective() );
		
		String toStringResult = okTestResultSkip.toString();
		
		assertNotNull( okTestResultSkip.getComment() );
		
		final String expectedValue = "ok " + okTestResultSkip.getTestNumber() 
		+ okTestResultSkip.getDirective() + " # This status is set to true in another method.";
		
		assertEquals( toStringResult, expectedValue );

	}
	
}
