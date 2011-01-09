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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTestSet 
extends Assert
{
	
	protected TestSet testSet;
	
	protected Header header;
	
	protected Plan plan;
	
	protected Footer footer;
	
	protected BailOut bailOut;
	
	protected Comment comment;
	
	protected TestResult tr1;
	
	protected Text text;
	
	@BeforeMethod
	public void setUp()
	{
		testSet = new TestSet();
		
		// Header
		header = new Header( 13 );
		testSet.setHeader( header );
		
		// Plan
		plan = new Plan(3);
		testSet.setPlan( plan );
		
		// Comment
		comment = new Comment( "Starting tests..." );
		testSet.addComment( comment );
		
		// Test Results
		tr1 = new TestResult(StatusValues.OK, 1);
		testSet.addTestResult( tr1 );
		
		text = new Text("Ignore this line please.");
		testSet.addTapLine( text );
		
		// Bail Out
		bailOut = new BailOut( "Uck!" );
		testSet.addBailOut( bailOut );
		
		// Footer
		footer = new Footer("TestSet Footer");
		testSet.setFooter( footer );
	}
	
	@Test
	public void testTestSet()
	{
		assertNotNull( this.testSet );
		
		assertNotNull( this.testSet.getHeader() );
		
		assertNotNull( this.testSet.getPlan() );
		
		assertTrue( this.testSet.getComments().size() == 1 );
		
		assertTrue( this.testSet.getNumberOfComments() == 1 );
		
		assertTrue( this.testSet.getNumberOfTapLines() == this.testSet.getTapLines().size() );
		
		assertTrue( this.testSet.getNumberOfTapLines() == 4 );
		
		assertTrue( this.testSet.getNumberOfTestResults() == this.testSet.getTestResults().size() );
		
		assertTrue( this.testSet.getNumberOfTestResults() == 1 );
		
		assertTrue( this.testSet.getNextTestNumber() == 2 );
		
		assertTrue( this.testSet.hasBailOut() );
		
		assertTrue (this.testSet.getBailOuts().size() == 1 );
		
		assertTrue( this.testSet.getNumberOfBailOuts() == 1 );
		
		assertNotNull( this.testSet.getSummary() );
		
		assertEquals( this.testSet.toString(), this.testSet.getDetails() );
		
		assertNotNull( this.testSet.getFooter() );
	}
	
	@Test
	public void intrusiveTests()
	{
		this.testSet.removeBailOut( bailOut );
		assertTrue( this.testSet.getNumberOfBailOuts() == 0 );
		
		this.testSet.removeComment( comment );
		assertTrue( this.testSet.getNumberOfComments() == 0 );
		assertFalse( this.testSet.removeComment( comment ) );
		
		this.testSet.removeTestResult( tr1 );
		assertTrue( this.testSet.getTestResults().size() == 0 );
		assertFalse( this.testSet.removeTestResult( tr1 ));
		
		this.testSet.removeTapLine( text );
		assertTrue( this.testSet.getTapLines().size() == 0 );
	}

}
