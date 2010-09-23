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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public interface TapConsumer
{

	/* -- Reading methods -- */
	
	/**
	 * Parses a Test Result.
	 * 
	 * @param testResult Test Result line
	 */
	public void parseLine( String testResult ) 
	throws TapParserException;
	
	/**
	 * Parses a Tap Stream.
	 * 
	 * @param tapStream Tap Stream
	 */
	public void parseTapStream( String tapStream ) 
	throws TapParserException;
	
	/**
	 * Parses a Tap File.
	 * 
	 * @param tapFile Tap File
	 */
	public void parseFile( File tapFile ) 
	throws TapParserException;
	
	/* -- Test Execution information methods -- */
	
	/**
	 * @return The Header of the Tap File
	 */
	public String getHeader();
	
	/**
	 * @return TAP version
	 */
	public Integer getTapVersion();

	/** 
	 * @return true if the executor should ignore all tests, false otherwise
	 */
	public Boolean isSkipAllTests();
	
	/**
	 * @return Reason to skip all tests
	 */
	public String getSkipAllTestsReason();
	
	/**
	 * @return Number of tests
	 */
	public Integer getNumberOfTests();
	
	/**
	 * @return Number of comments
	 */
	public Integer getNumberOfComments();
	
	/**
	 * @return List of comments
	 */
	public List<String> getListOfComments();
	
	/**
	 * @param testNumber Number of test to be retrieved
	 * @return A single test result
	 */
	public TestResult getTestResult( Integer testNumber );
	
	/**
	 * @return List of Test Results
	 */
	public List<TestResult> getListOfTestResults();
	
	/**
	 * @return true if any not ok occurred
	 */
	public Boolean containsNotOk();
	
	/**
	 * @return true if the Stream contains any Bail Out expression
	 */
	public Boolean containsBailOut();
	
	public List<String> getListOfBailOuts();
	
	public String getFooter();
	
	/* -- Output methods -- */
	
	public void printSummary( PrintWriter pw );
	
	public void printSummary( PrintStream ps );
	
	public void printDetails( PrintWriter pw );
	
	public void printDetails(PrintStream ps);
	
}
