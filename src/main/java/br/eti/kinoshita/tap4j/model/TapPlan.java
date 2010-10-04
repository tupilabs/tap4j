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

import java.io.Serializable;


/**
 * TAP Plan. The TAP Plan gives details about the execution of the tests such 
 * as initial test number, last test number, flag to skip all tests and 
 * a reason for this.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TapPlan 
implements Serializable
{

	/**
	 * TAP Plan initial test number.
	 */
	private Integer initialTestNumber;
	
	/**
	 * TAP Plan last test number.
	 */
	private Integer lastTestNumber;
	
	/**
	 * TAP Plan skip. If present the tests should not be executed.
	 */
	private SkipPlan skip;
	
	/**
	 * A comment.
	 */
	private Comment comment;
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param initialTestNumber Initial test number (usually is 1).
	 * @param lastTestNumber Last test number (may be 0 if to skip all tests).
	 */
	public TapPlan( Integer initialTestNumber, Integer lastTestNumber )
	{
		this.initialTestNumber = initialTestNumber;
		this.lastTestNumber = lastTestNumber;
	}
	
	/**
	 * Constructor with parameters
	 * 
	 * @param initialTestNumber Initial test number (usually is 1).
	 * @param lastTestNumber Last test number (may be 0 if to skip all tests).
	 * @param skip Plan skip with a reason.
	 */
	public TapPlan( 
			Integer initialTestNumber, 
			Integer lastTestNumber, 
			SkipPlan skip)
	{
		this.initialTestNumber = initialTestNumber;
		this.lastTestNumber = lastTestNumber;
		this.skip = skip;
	}
	
	/**
	 * @return Initial test number.
	 */
	public Integer getInitialTestNumber()
	{
		return this.initialTestNumber;
	}
	
	/**
	 * @return Last test number.
	 */
	public Integer getLastTestNumber()
	{
		return this.lastTestNumber;
	}
	
	/**
	 * @return Flag used to indicate whether skip all tests or not.
	 * @see {@link #getSkip()}
	 */
	public Boolean isSkip()
	{
		return this.skip != null;
	}
	
	/**
	 * @return Plan Skip with reason.
	 * @see {@link #isSkip()}
	 */
	public SkipPlan getSkip()
	{
		return this.skip;
	}
	
	/**
	 * Defines whether we should skip all tests or not.
	 * 
	 * @param skip Plan Skip. 
	 */
	public void setSkip( SkipPlan skip )
	{
		this.skip = skip;
	}

	/**
	 * @return Optional Plan comment.
	 */
	public Comment getComment()
	{
		return this.comment;
	}
	
	/**
	 * Sets a comment into the Plan.
	 * 
	 * @param comment Plan comment.
	 */
	public void setComment( Comment comment )
	{
		this.comment = comment;
	}
	
	/**
	 * <p>Returns the test plan as follows:</p>
	 * 
	 *  <p><i>&lt;initial test number&gt;..&lt;last test number&gt;
	 *  SPACE["skip"]SPACE[&lt;reason&gt;]</i></p>
	 * 
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append( initialTestNumber );
		sb.append( ".." );
		sb.append( lastTestNumber);
		if ( this.skip != null )
		{
			sb.append( " skip " );
			sb.append( this.skip.getReason() );
		}
		
		if ( this.comment != null )
		{
			sb.append( this.comment.toString() );
		}
		
		return sb.toString();
	}
	
}
