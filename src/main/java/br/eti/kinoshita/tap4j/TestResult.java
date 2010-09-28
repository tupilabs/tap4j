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

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestResult 
implements TapLine
{
	/**
	 * Test Status (OK, NOT OK).
	 */
	private StatusValues testStatus;

	/**
	  * Test Number.
	  */
	private Integer testNumber;
	
	/**
	 * Description of the test.
	 */
	private String description;
	
	/**
	 * Directive of the test (TODO, SKIP).
	 */
	private Directive directive;

	/**
	 * Constructor with parameter.
	 * 
	 * @param testStatus Status of the test.
	 */
	public TestResult( StatusValues testStatus )
	{
		this.testStatus = testStatus;
	}

	/**
	 * @return Status of the test.
	 */
	public StatusValues getTestStatus()
	{
		return this.testStatus;
	}

	/**
	 * @param testStatus Status of the test.
	 */
	public void setTestStatus( StatusValues testStatus )
	{
		this.testStatus = testStatus;
	}
	
	/**
	 * @return Test Number.
	 */
	public Integer getTestNumber()
	{
		return this.testNumber;
	}
	
	/**
	 * @param testNumber Test Number.
	 */
	public void setTestNumber( Integer testNumber )
	{
		this.testNumber = testNumber;
	}
	
	/**
	 * @return Test description.
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * @param description Test description.
	 */
	public void setDescription( String description )
	{
		this.description = description;
	}
	
	/**
	 * @return Optional Directive.
	 */
	public Directive getDirective()
	{
		return this.directive;
	}
	
	/**
	 * @param testDirective Optional Directive.
	 */
	public void setDirective( Directive directive )
	{
		this.directive = directive;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append( Util.getStatusText(testStatus) );
		
		Util.appendIfNotNull(sb, " ", testNumber, null);
		Util.appendIfNotNull(sb, " ", description, null);
		
		Util.appendIfNotNull(sb, " # ", directive, null);
		
		return sb.toString();
	}

}
