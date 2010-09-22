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

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 20/09/2010
 */
public class TestResult 
implements Serializable
{

	private static final char CHAR_SPACE = ' ';
	private static final String CHAR_SHARP = "#";
	
	/**
	 * Sequence number of the test.
	 */
	private Integer testNumber;
	
	/**
	 * Status of execution.
	 */
	private Status status;
	
	/**
	 * Details about execution.
	 */
	private String result;
	
	/* -- Constructors -- */
	
	/**
	 * Default constructor.
	 */
	public TestResult()
	{
		super();
	}
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param testNumber Test sequence number
	 * @param status Test execution status
	 * @param result Test execution result
	 * @param comment Any comment about the test execution
	 */
	public TestResult(
			Integer testNumber, 
			Status status, 
			String result,
			String comment)
	{
		super();
		this.testNumber = testNumber;
		this.status = status;
		this.result = result;
		this.comment = comment;
	}

	/**
	 * Any comment about execution.
	 */
	private String comment;

	/* -- getters and setters -- */
	
	/**
	 * @return the testNumber
	 */
	public Integer getTestNumber()
	{
		return testNumber;
	}

	/**
	 * @param testNumber The sequence of a test result
	 */
	public void setTestNumber( Integer testNumber )
	{
		this.testNumber = testNumber;
	}

	/**
	 * @return the status
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus( Status status )
	{
		this.status = status;
	}

	/**
	 * @return the result
	 */
	public String getResult()
	{
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult( String result )
	{
		this.result = result;
	}

	/**
	 * @return the comment
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment( String comment )
	{
		this.comment = comment;
	}
	
	/**
	 * Adds a directive to the test result.
	 * 
	 * @param directive Directive
	 * @param reason Reason
	 */
	public void addDirective( Directive directive, String reason )
	{
		this.comment =  
			Util.getDirectiveText(directive) + 
			CHAR_SPACE + 
			reason;
	}
	
	/* -- utility methods -- */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer();
		
		if ( this.status != null && this.testNumber > 0 )
		{
			sb.append( Util.getStatusText( this.status ) );
			sb.append( CHAR_SPACE );
			sb.append( this.testNumber );
		}
		
		if ( this.result != null && this.result.trim().length() > 0 )
		{
			sb.append( CHAR_SPACE );
			sb.append( this.result );
		}
		
		if ( this.comment != null )
		{
			sb.append( CHAR_SPACE );
			sb.append( CHAR_SHARP );
			sb.append( CHAR_SPACE );
			sb.append( this.comment );
		}
		
		return sb.toString();
	}	
	
}
