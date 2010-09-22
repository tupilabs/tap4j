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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public abstract class AbstractTapProducer 
implements TapProducer, Serializable
{

	/**
	 * TAP version.
	 */
	protected Integer tapVersion;
	
	/**
	 * Skip All flag.
	 */
	protected Boolean skipAll = false;
	
	/**
	 * Reason to skip all tests.
	 */
	protected String skipAllreason;
	
	/**
	 * List of Test Results.
	 */
	protected final List<Object> testResultsList = new ArrayList<Object>();
	
	/**
	 * Footer of the TAP Stream
	 */
	protected Comment footer = new Comment();
	
	/**
	 * Number of tests addded to this Tap Producer.
	 */
	protected Integer numberOfTests = 0;
	
	/**
	 * Constructor of a TAP Producer. You should inform the version of the 
	 * TAP protocol.
	 * 
	 * @param tapVersion Version of the TAP protocol.
	 */
	public AbstractTapProducer(Integer tapVersion)
	{
		this.tapVersion = tapVersion;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#setVersion(java.lang.Integer)
	 */
	public void setVersion( Integer tapVersion )
	{
		this.tapVersion = tapVersion;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#getVersion()
	 */
	public Integer getVersion()
	{
		return this.tapVersion;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#setSkipAll(boolean)
	 */
	public void setSkipAll( boolean skipAll, String skipAllreason )
	{
		this.skipAll = skipAll;
		this.skipAllreason = skipAllreason;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#getSkipAll()
	 */
	public boolean getSkipAll()
	{
		return this.skipAll;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#getSkipAllReason()
	 */
	public String getSkipAllReason()
	{
		return this.skipAllreason;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#setTestResult(br.eti.kinoshita.tap4j.TestResult)
	 */
	public void addTestResult( TestResult result )
	{
		this.testResultsList.add( result );
		this.numberOfTests += 1;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#setTestResult(java.lang.Integer, br.eti.kinoshita.tap4j.TestStatus, java.lang.String, java.lang.String)
	 */
	public void addTestResult( 
			Integer testNumber, 
			Status status, 
			String result,
			String comment )
	{
		TestResult testResult = new TestResult(testNumber, status, result, comment);
		this.addTestResult( testResult );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#addComment(java.lang.String)
	 */
	public void addComment( String text )
	{
		Comment comment = new Comment ( text );
		this.testResultsList.add( comment );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#addComment(br.eti.kinoshita.tap4j.Comment)
	 */
	public void addComment( Comment comment )
	{
		this.testResultsList.add( comment );		
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#addBailOut(java.lang.String)
	 */
	public void addBailOut( String reason )
	{
		this.testResultsList.add( "Bail out! " + reason );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#setFooter(java.lang.String)
	 */
	public void setFooter( String footer )
	{
		this.footer = new Comment( footer );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#getTapStream()
	 */
	public String getTapStream()
	{
		final StringBuffer tapStream = new StringBuffer();
		
		this.makeTapStreamHeader( tapStream );
		this.makeTapStreamPlan( tapStream );
		this.makeTapStreamBody( tapStream );
		this.makeTapStreamFooter( tapStream );
		
		return tapStream.toString();
	}

	/* -- Utility methods -- */
	/**
	 * Prepares the Header of the TAP Stream.
	 * 
	 * @param tapStream
	 */
	protected void makeTapStreamHeader( StringBuffer tapStream )
	{
		tapStream.append("TAP version " + this.tapVersion.toString());
		tapStream.append( System.getProperty("line.separator") );
	}
	
	/**
	 * Prepares the Test Plan of the TAP Stream.
	 * 
	 * @param tapStream
	 */
	protected void makeTapStreamPlan( StringBuffer tapStream )
	{
		if ( this.getSkipAll() )
		{
			tapStream.append( "1..0 skip " + this.getSkipAllReason() );
		} 
		else 
		{
			tapStream.append( "1.." + this.numberOfTests );
		}
		tapStream.append( System.getProperty("line.separator") );
	}
	
	/**
	 * Prepares the Body of the TAP Steam.
	 * 
	 * @param tapStream
	 */
	protected void makeTapStreamBody( StringBuffer tapStream )
	{
		
		Iterator<Object> it = testResultsList.iterator(); 
		
		while ( it.hasNext() )
		{
			Object obj = it.next();
			
			if ( obj instanceof TestResult )
			{
				TestResult testResult = (TestResult) obj;
				tapStream.append( testResult.toString() );
				tapStream.append( System.getProperty("line.separator") );
			} 
			else if ( obj instanceof Comment )
			{
				Comment comment = (Comment) obj;
				tapStream.append( comment.toString() );
				tapStream.append( System.getProperty("line.separator") );
			}
			else 
			{
				// throw new RuntimeException( "Invalid object in result list: " + obj.getClass() );
				tapStream.append( obj.toString() );
				tapStream.append( System.getProperty("line.separator") );
			}
		}
	}
	
	/**
	 * Prepares Tap Stream footer.
	 * 
	 * @param tapStream
	 */
	protected void makeTapStreamFooter( StringBuffer tapStream )
	{
		if ( this.footer.getText() != null && this.footer.getText().trim().length() > 0 )
		{
			tapStream.append( this.footer.toString() );
		}
	}
	
}
