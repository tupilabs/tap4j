/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * @since 1.0
 * @see http://www.digitalsandwich.com/test-harness.php
 */
public abstract class AbstractTapConsumer 
implements TapConsumer
{
	
	// Useful entities
	
	protected List<Comment> listOfComments = new ArrayList<Comment>();
	
	// Header
	
	protected String tapHeader = null;
	
	protected Integer tapVersion = 0;
	
	// Plan

	protected TestTAPPlan testPlan = null;
	
	// Body
	
	protected Integer numberOfTests = 0;
	
	protected Boolean containsNotOk = false;
	
	protected Boolean containsBailOut = false;
	
	protected List<String> listOfBailOuts = new ArrayList<String>();
	
	protected List<TestResult> listOfTestResults = new ArrayList<TestResult>();
	
	// Footer
	
	protected String footer;
	
	// REGEX
	
	Pattern testLineRegex = Pattern.compile( REGEX_TEST_LINE );
	Pattern testPlanRegex = Pattern.compile( REGEX_TEST_PLAN );
	
	public AbstractTapConsumer()
	{

	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseFile(java.io.File)
	 */
	public void parseFile( File tapFile ) 
	throws TapParserException
	{
		if ( tapFile == null || ! tapFile.canRead() )
		{
			throw new TapParserException("Can not read tap file " + tapFile );
		}
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner( tapFile );
			
			while ( scanner.hasNextLine())
			{
				this.parseLine( scanner.nextLine() );
			}
		}
		catch (FileNotFoundException e) 
		{
			throw new TapParserException("Failed reading tap file: " + e.getMessage(), e);
		} 
		finally 
		{
			scanner.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseLine(java.lang.String)
	 */
	public void parseLine(String testResultText) 
	throws TapParserException 
	{
		// Is is t TestResult?
		Matcher m = testLineRegex.matcher( testResultText );
		if ( m.matches() )
		{
			TestResult testResult = new TestResult();
			
			String okOrNotOk = m.group(1);
			if ( okOrNotOk.trim().equals("ok"))
			{
				testResult.setStatus(Status.OK);
			}
			else 
			{
				testResult.setStatus(Status.NOT_OK);
			}
			testResult.setTestNumber(Integer.parseInt(m.group(2)));
			testResult.setResult(m.group(3));
			
			if ( m.group(4) != null )
			{
				String directiveText = m.group(5);
				if ( directiveText.trim().equals("TODO"))
				{
					testResult.setDirective(Directive.TODO);
				} else
				{
					testResult.setDirective(Directive.SKIP);
				}
				
			}
			
			this.listOfTestResults.add( testResult );
		}
		
		m = testPlanRegex.matcher( testResultText );
		if ( m.matches() )
		{
			testPlan = new TestTAPPlan();
			try
			{
				testPlan.setFirstStep(Integer.parseInt(m.group(1)));
				testPlan.setLastStep(Integer.parseInt(m.group(3)));
				
				Object commentPresent = m.group(6);
				if ( commentPresent != null )
				{
					Comment comment = new Comment();
					comment.setText( m.group(7) );
					testPlan.setComment( comment );
					this.listOfComments.add(comment);
				}
				
				if ( m.group (4) != null )
				{
					testPlan.setSkipAll(true);
					testPlan.setSkipAllReason( m.group( 5 ) );
				}
				
			} 
			catch ( NumberFormatException nfe )
			{
				throw new TapParserException(nfe);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseTapStream(java.lang.String)
	 */
	public void parseTapStream(String tapStream) 
	throws TapParserException {
		if ( tapStream == null || tapStream.trim().length() <= 0 )
		{
			throw new TapParserException("Invalid Tap Stream " + tapStream );
		}
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner( tapStream );
			
			while ( scanner.hasNextLine())
			{
				this.parseLine( scanner.nextLine() );
			}
		}
		finally 
		{
			scanner.close();
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getHeader()
	 */
	public String getHeader() {
		return tapHeader;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTestPlan()
	 */
	public TestTAPPlan getTestPlan() 
	{
		return this.testPlan;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTapVersion()
	 */
	public Integer getTapVersion() {
		return tapVersion;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#isSkipAllTests()
	 */
	public Boolean isSkipAllTests() 
	{
		return testPlan != null ? testPlan.isSkipAll() : false;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getSkipAllTestsReason()
	 */
	public String getSkipAllTestsReason() 
	{
		return (testPlan != null && testPlan.isSkipAll()) ? testPlan.getSkipAllReason() : null;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfTests()
	 */
	public Integer getNumberOfTestResults() 
	{
		return this.listOfTestResults.size();
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfComments()
	 */
	public Integer getNumberOfComments() {
		return listOfComments.size();
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getListOfComments()
	 */
	public List<Comment> getListOfComments() {
		return listOfComments;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTestResult(java.lang.Integer)
	 */
	public _TestResult getTestResult(Integer testNumber) {
		for(_TestResult result : listOfTestResults)
		{
			if ( result.getTestNumber() == testNumber )
			{
				return result;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getListOfTestResults()
	 */
	public List<_TestResult> getListOfTestResults() {
		return listOfTestResults;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#containsNotOk()
	 */
	public Boolean containsNotOk() {
		return containsNotOk;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#containsBailOut()
	 */
	public Boolean containsBailOut() {
		return containsBailOut;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getListOfBailOuts()
	 */
	public List<String> getListOfBailOuts() {
		return listOfBailOuts;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getFooter()
	 */
	public String getFooter() {
		return footer;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printSummary(java.io.PrintWriter)
	 */
	public void printSummary(PrintWriter pw) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printSummary(java.io.PrintStream)
	 */
	public void printSummary(PrintStream ps) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printDetails(java.io.PrintWriter)
	 */
	public void printDetails(PrintWriter pw) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printDetails(java.io.PrintStream)
	 */
	public void printDetails(PrintStream ps) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
	
}
