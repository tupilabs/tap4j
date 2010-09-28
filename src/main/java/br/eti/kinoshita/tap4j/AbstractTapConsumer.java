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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract TAP Consumer. Implements few basic methods.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public abstract class AbstractTapConsumer 
implements TapConsumer
{

	/**
	 * Test Set.
	 */
	protected TestSet testSet;
	
	/**
	 * Header.
	 */
	protected Header header;
	
	/**
	 * Plan.
	 */
	protected Plan plan;
	
	/**
	 * List of TAP Lines (test results, bail outs and comments).
	 */
	protected List<TapLine> tapLines = new ArrayList<TapLine>();
	
	/**
	 * List of Test Results.
	 */
	protected List<TestResult> testResults = new ArrayList<TestResult>();
	
	/**
	 * List of Bail Outs.
	 */
	protected List<BailOut> bailOuts = new ArrayList<BailOut>();
	
	/**
	 * List of Comments.
	 */
	protected List<Comment> comments = new ArrayList<Comment>();
	
	/**
	 * Footer.
	 */
	protected Footer footer;
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getHeader()
	 */
	public Header getHeader()
	{
		return this.header;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getPlan()
	 */
	public Plan getPlan()
	{
		return this.plan;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTapLines()
	 */
	public List<TapLine> getTapLines()
	{
		return this.tapLines;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfTapLines()
	 */
	public Integer getNumberOfTapLines()
	{
		return this.tapLines.size();
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTestResults()
	 */
	public List<TestResult> getTestResults()
	{
		return this.testResults;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getTestResult(java.lang.Integer)
	 */
	public TestResult getTestResult( Integer testNumber )
	{
		TestResult foundTestResult = null;
		
		for( TestResult testResult : this.testResults )
		{
			if ( testResult.getTestNumber() != null && testResult.getTestNumber().equals(testNumber) )
			{
				foundTestResult = testResult;
				break;
			}
		}
		
		return foundTestResult;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#containsOk()
	 */
	public Boolean containsOk()
	{
		Boolean containsOk = false;
		
		for( TestResult testResult : this.testResults )
		{
			if ( testResult.getStatus().equals( StatusValues.OK ) )
			{
				containsOk = true;
				break;
			}
		}
		
		return containsOk;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#containsNotOk()
	 */
	public Boolean containsNotOk()
	{
		Boolean containsNotOk = false;
		
		for( TestResult testResult : this.testResults )
		{
			if ( testResult.getStatus().equals( StatusValues.NOT_OK ) )
			{
				containsNotOk = true;
				break;
			}
		}
		
		return containsNotOk;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfTestResults()
	 */
	public Integer getNumberOfTestResults()
	{
		return this.testResults.size();
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#containsBailOut()
	 */
	public Boolean containsBailOut()
	{
		return this.bailOuts.size() > 0;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getBailOuts()
	 */
	public List<BailOut> getBailOuts()
	{
		return this.bailOuts;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfBailOuts()
	 */
	public Integer getNumberOfBailOuts()
	{
		return this.bailOuts.size();
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getComments()
	 */
	public List<Comment> getComments()
	{
		return this.comments;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getNumberOfComments()
	 */
	public Integer getNumberOfComments()
	{
		return this.comments.size();
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#getFooter()
	 */
	public Footer getFooter()
	{
		return this.footer;
	}

	public TestSet getTestSet()
	{
		testSet = new TestSet();
		
		testSet.setHeader( this.header );
		testSet.setPlan( this.plan );
		
		for ( TapLine tapLine : tapLines )
		{
			testSet.addTapLine( tapLine );
		}
		
		testSet.setFooter( this.footer );
		
		return testSet;
	}
	
}
