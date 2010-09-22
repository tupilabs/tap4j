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

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public interface TapProducer
{
	
	/* -- Header -- */
	public void setVersion( Integer version );
	
	public Integer getVersion();
	
	/* -- Plan -- */
	public void setSkipAll( boolean skipAll, String skipAllreason );
	
	/**
	 * Returns whether the all the tests should be skipped or not.
	 * 
	 * @return Skip all flag
	 */
	public boolean getSkipAll();
	
	/**
	 * Returns the reason to skip all tests.
	 * 
	 * @return Skip all reason
	 */
	public String getSkipAllReason();
	
	/* -- Test Result -- */
	/**
	 * Sets a test result.
	 * 
	 * @param testNumber The sequence number of a test
	 * @param status Status of execution
	 * @param result Details about execution
	 * @param comment Any comment about execution
	 */
	public void addTestResult(Integer testNumber, Status status, String result, String comment);
	
	/**
	 * Sets a test result.
	 * 
	 * @param result Test result
	 */
	public void addTestResult(TestResult result);
	
	/* -- Comment -- */
	/**
	 * Adds a comment.
	 * 
	 * @param text Text of the comment
	 */
	public void addComment( String text );
	
	/**
	 * Adds a comment.
	 * 
	 * @param comment Comment object
	 */
	public void addComment( Comment comment );
	
	/**
	 * Adds the footer of the TAP Stream.
	 * 
	 * @param string Footer
	 */
	public void setFooter( String footer );
	
	/**
	 * Adds bail out reason.
	 * 
	 * @param reason Bail out reason
	 */
	public void addBailOut( String reason );
	
	/**
	 * Returns the TAP Stream.
	 * 
	 * @return TAP Stream
	 */
	public String getTapStream();
	
	/**
	 * Printing a TAP Producer outputs its TAP Stream.
	 * 
	 * @return TAP Stream
	 */
	public String toString();

}
