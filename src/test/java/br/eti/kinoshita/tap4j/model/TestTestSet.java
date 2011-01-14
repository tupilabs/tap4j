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
		Assert.assertNotNull( this.testSet );
		
		Assert.assertNotNull( this.testSet.getHeader() );
		
		Assert.assertNotNull( this.testSet.getPlan() );
		
		Assert.assertTrue( this.testSet.getComments().size() == 1 );
		
		Assert.assertTrue( this.testSet.getNumberOfComments() == 1 );
		
		Assert.assertTrue( this.testSet.getNumberOfTapLines() == this.testSet.getTapLines().size() );
		
		Assert.assertTrue( this.testSet.getNumberOfTapLines() == 4 );
		
		Assert.assertTrue( this.testSet.getNumberOfTestResults() == this.testSet.getTestResults().size() );
		
		Assert.assertTrue( this.testSet.getNumberOfTestResults() == 1 );
		
		Assert.assertTrue( this.testSet.getNextTestNumber() == 2 );
		
		Assert.assertTrue( this.testSet.hasBailOut() );
		
		Assert.assertTrue ( this.testSet.getBailOuts().size() == 1 );
		
		Assert.assertTrue( this.testSet.getNumberOfBailOuts() == 1 );
		
		Assert.assertNotNull( this.testSet.getSummary() );
		
		Assert.assertEquals( this.testSet.toString(), this.testSet.getDetails() );
		
		Assert.assertNotNull( this.testSet.getFooter() );
	}
	
	@Test(dependsOnMethods={"testTestSet"})
	public void intrusiveTests()
	{
		this.testSet.removeBailOut( bailOut );
		Assert.assertTrue( this.testSet.getNumberOfBailOuts() == 0 );
		
		this.testSet.removeComment( comment );
		Assert.assertTrue( this.testSet.getNumberOfComments() == 0 );
		Assert.assertFalse( this.testSet.removeComment( comment ) );
		
		this.testSet.removeTestResult( tr1 );
		Assert.assertTrue( this.testSet.getTestResults().size() == 0 );
		Assert.assertFalse( this.testSet.removeTestResult( tr1 ));
		
		this.testSet.removeTapLine( text );
		Assert.assertTrue( this.testSet.getTapLines().size() == 0 );
	}
	
	@Test
	public void testTestSetWithOnlyTestResults()
	{
		TestSet testSet = new TestSet();
		
		Assert.assertFalse( testSet.containsOk() );
		Assert.assertFalse( testSet.containsNotOk() );
		Assert.assertFalse( testSet.containsBailOut() );
		
		TestResult okTestResult = new TestResult();
		okTestResult.setStatus(StatusValues.OK);
		
		Assert.assertTrue( testSet.addTestResult( okTestResult ) );
		Assert.assertTrue( testSet.containsOk() );
		
		TestResult notOkTestResult = new TestResult();
		notOkTestResult.setStatus(StatusValues.NOT_OK);
		
		Assert.assertTrue( testSet.addTestResult( notOkTestResult ) );
		Assert.assertTrue( testSet.containsNotOk() );
		
		String summary = testSet.getSummary();
		Assert.assertFalse(summary.contains(".."));
		Assert.assertFalse(summary.contains("ersion 13"));
		
	}
	
	@Test
	public void testTestSetWithATestResultWithNullTestNumber()
	{
		TestSet testSet = new TestSet();
		
		Assert.assertFalse( testSet.containsOk() );
		Assert.assertFalse( testSet.hasBailOut() );
		
		Assert.assertFalse( testSet.removeBailOut(new BailOut("False bailout")));
		
		TestResult okTestResult = new TestResult();
		okTestResult.setStatus(StatusValues.OK);
		
		okTestResult.setTestNumber(null);
		Assert.assertTrue( testSet.addTestResult( okTestResult ) );
		Assert.assertTrue( testSet.containsOk() );
		
		TestResult testResult = testSet.getTestResult( new Integer(1) );
		
		Assert.assertNotNull( testResult );
		
		okTestResult.setTestNumber( new Integer(1) );
		Assert.assertTrue( testSet.addTestResult( okTestResult ) );
		
		testResult = testSet.getTestResult( new Integer(1) );
		
		Assert.assertNotNull( testResult );
	}
	
	@Test
	public void testWithOnlyNotOkTestResults()
	{
		TestSet testSet = new TestSet();
		
		TestResult notOkTestResult = new TestResult();
		notOkTestResult.setStatus( StatusValues.NOT_OK );
		
		Assert.assertFalse( testSet.containsNotOk() );
		Assert.assertFalse( testSet.containsOk() );
		
		Assert.assertTrue( testSet.addTestResult( notOkTestResult ) );
		
		Assert.assertTrue( testSet.containsNotOk() );
		Assert.assertFalse( testSet.containsOk() );
		
		String summary = testSet.getSummary();
		Assert.assertFalse( summary.contains("ersion 13") );
	}

}
