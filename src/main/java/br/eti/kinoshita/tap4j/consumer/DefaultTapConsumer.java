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
package br.eti.kinoshita.tap4j.consumer;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.BailOut;
import br.eti.kinoshita.tap4j.model.Comment;
import br.eti.kinoshita.tap4j.model.Directive;
import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.SkipPlan;
import br.eti.kinoshita.tap4j.model.TapResult;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.model.Text;
import br.eti.kinoshita.tap4j.util.DirectiveValues;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class DefaultTapConsumer 
extends AbstractTapConsumer
{
	/* -- Regular expressions -- */
	
	public static final String REGEX_HEADER = "\\s*TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";
	
	public static final String REGEX_PLAN = "\\s*(\\d)+(\\.{2})(\\d)+\\s*(skip\\s*([^#]+))?\\s*(#\\s*(.*))?";
	
	public static final String REGEX_TEST_RESULT = "\\s*(ok|not ok)\\s*(\\d+)?\\s*([^#]*)?\\s*(#\\s*(SKIP|TODO)\\s*([^#]+))?\\s*(#\\s*(.*))?"; 
	
	public static final String REGEX_BAIL_OUT = "\\s*Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";
	
	public static final String REGEX_COMMENT = "\\s*#\\s*(.*)";
	
	public static final String REGEX_FOOTER = "\\s*TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";
	
	/* -- REGEX -- */
	protected Pattern headerREGEX = Pattern.compile( REGEX_HEADER );
	protected Pattern planREGEX = Pattern.compile( REGEX_PLAN );
	protected Pattern testResultREGEX = Pattern.compile( REGEX_TEST_RESULT );
	protected Pattern bailOutREGEX = Pattern.compile ( REGEX_BAIL_OUT );
	protected Pattern commentREGEX = Pattern.compile ( REGEX_COMMENT );
	protected Pattern footerREGEX = Pattern.compile ( REGEX_FOOTER );
	
	protected boolean isFirstLine = true;
	
	protected boolean isHeaderSet = false;
	protected boolean isPlanSet = false;
	
	protected boolean isPlanBeforeTestResult = false;
	
	// Helper String to check the Footer
	protected String lastLine = null;
	
	public boolean isPlanBeforeTestResult()
	{
		return this.isPlanBeforeTestResult;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseLine(java.lang.String)
	 */
	public void parseLine( String tapLine ) 
	throws TapParserException
	{
		if ( StringUtils.isEmpty( tapLine ) )
		{
			return;
		}
		
		Matcher matcher = null;
		
		// Comment
		matcher = commentREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractComment( matcher );
			return;
		}
		
		// Last line that is not a comment.
		lastLine = tapLine;
		
		// Header 
		matcher = headerREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			
			this.checkTAPHeaderParsingLocationAndDuplicity();
			
			this.extractHeader ( matcher );
			this.isFirstLine = false;
			return;
		}
		
		// Plan 
		matcher = planREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			
			this.checkTAPPlanDuplicity();
			
			this.checkIfTAPPlanIsSetBeforeTestResultsOrBailOut();
			
			this.extractPlan ( matcher);
			this.isFirstLine = false;
			return;
		}
		
		// Test Result
		matcher = testResultREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractTestResult ( matcher );
			return;
		}
		
		// Bail Out
		matcher = bailOutREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractBailOut( matcher );
			return;
		}
		
		// Footer
		matcher = footerREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractFooter( matcher );
			return;
		}
		
		// Any text. It should not be parsed by the consumer.
		final Text text = new Text( tapLine );
		this.tapLines.add( text );
	}

	/**
	 * Checks if the TAP Plan is set before any Test Result or Bail Out.
	 */
	protected void checkIfTAPPlanIsSetBeforeTestResultsOrBailOut()
	{
		if ( this.testResults.size() <= 0 && this.bailOuts.size() <= 0 )
		{
			this.isPlanBeforeTestResult = true;
		}
	}

	/**
	 * Checks the Header location and duplicity. The Header must be the first 
	 * element and cannot occurs more than on time. However the Header is 
	 * optional.
	 */
	protected void checkTAPHeaderParsingLocationAndDuplicity() 
	throws TapParserException
	{
		if ( isHeaderSet )
		{
			throw new TapParserException( "Duplicated TAP Header found." );
		}
		if ( ! isFirstLine )
		{
			throw new TapParserException( "Invalid position of TAP Header. It must be the first element (apart of Comments) in the TAP Stream." );
		}
		isHeaderSet = true;
	}
	
	/**
	 * Checks if there are more than one TAP Plan in the TAP Stream. 
	 */
	protected void checkTAPPlanDuplicity() 
	throws TapParserException
	{
		if ( isPlanSet )
		{
			throw new TapParserException( "Duplicated TAP Plan found." );
		}
		isPlanSet = true;
	}

	/**
	 * This method is called after the TAP Stream has already been parsed. 
	 * So we just check if the plan was found before test result or bail outs. 
	 * If so, skip this check. Otherwise, we shall check if the last line 
	 * is the TAP Plan.
	 */
	protected void checkTAPPlanPosition() 
	throws TapParserException
	{
		if ( ! this.isPlanBeforeTestResult )
		{
			Matcher matcher = planREGEX.matcher( lastLine );
			
			if ( matcher.matches() )
			{
				return; // OK
			}
			
			throw new TapParserException("Invalid position of TAP Plan.");
		}
	}
	
	/**
	 * Extracts the Header from a TAP Line.
	 * 
	 * @param matcher REGEX Matcher.
	 */
	protected void extractHeader( Matcher matcher )
	{
		final Integer version = Integer.parseInt( matcher.group( 1 ) );
		
		final Header header = new Header( version );
		
		final String commentToken = matcher.group( 2 );
		
		if ( commentToken != null )
		{
			String text = matcher.group( 3 );
			final Comment comment = new Comment ( text );
			header.setComment( comment );
		}
		
		this.header = header;
	}
	
	/**
	 * @param matcher REGEX Matcher.
	 */
	protected void extractPlan( Matcher matcher )
	{
		Integer initialTest = Integer.parseInt( matcher.group(1) );
		Integer lastTest = Integer.parseInt( matcher.group(3) );
		
		Plan plan = null;
		plan = new Plan( initialTest, lastTest );
		
		String skipToken = matcher.group(4);
		if ( skipToken != null )
		{
			String reason = matcher.group( 5 );
			final SkipPlan skip = new SkipPlan( reason );
			plan.setSkip(skip);
		}
		
		String commentToken = matcher.group( 6 );
		if ( commentToken != null )
		{
			String text = matcher.group ( 7 );
			final Comment comment = new Comment( text );
			plan.setComment( comment );
		}
		
		this.plan = plan;
	}

	/**
	 * @param matcher REGEX Matcher.
	 */
	protected void extractTestResult( Matcher matcher )
	{
		TestResult testResult = null;
		
		final String okOrNotOk = matcher.group(1);
		StatusValues status = null;
		if ( okOrNotOk.trim().equals("ok"))
		{
			status = StatusValues.OK;
		}
		else // regex mate...
		{
			status = StatusValues.NOT_OK;
		}
		
		Integer testNumber = Integer.parseInt(matcher.group(2));
		testResult = new TestResult( status, testNumber );			
		
		testResult.setDescription(matcher.group(3));
		
		String directiveToken = matcher.group(4);
		if ( directiveToken != null )
		{
			String directiveText = matcher.group(5);
			DirectiveValues directiveValue = null;
			if ( directiveText.trim().equals("TODO"))
			{
				directiveValue = DirectiveValues.TODO;
			} else
			{
				directiveValue = DirectiveValues.SKIP;
			}
			String reason = matcher.group( 6 );
			Directive directive = new Directive( directiveValue, reason );
			testResult.setDirective( directive );
		}
		
		String commentToken = matcher.group( 7 );
		if ( commentToken != null )
		{
			String text = matcher.group ( 8 );
			final Comment comment = new Comment( text );
			testResult.setComment( comment );
		}
		
		this.testResults.add( testResult );
		this.tapLines.add( testResult );
	}
	
	/**
	 * @param matcher REGEX Matcher.
	 */
	protected void extractBailOut( Matcher matcher )
	{
		String reason = matcher.group(1);
		
		BailOut bailOut = new BailOut( reason );
		
		String commentToken = matcher.group( 2 );
		
		if ( commentToken != null )
		{
			String text = matcher.group( 3 );
			Comment comment = new Comment( text );
			bailOut.setComment( comment );
		}
		
		this.bailOuts.add( bailOut );
		this.tapLines.add( bailOut );
	}
	
	/**
	 * @param matcher REGEX Matcher.
	 */
	protected void extractComment( Matcher matcher )
	{
		String text = matcher.group ( 1 );
		Comment comment = new Comment ( text );
		
		this.comments.add( comment );
		this.tapLines.add( comment );
	}
	
	/**
	 * Simply extracts the footer from the TAP line.
	 * 
	 * @param matcher REGEX Matcher.
	 */
	protected void extractFooter( Matcher matcher )
	{
		String text = matcher.group ( 1 );				
		Footer footer = new Footer( text );
		
		final String commentToken = matcher.group( 2 );
		
		if ( commentToken != null )
		{
			String commentText = matcher.group( 3 );
			final Comment comment = new Comment ( commentText );
			footer.setComment( comment );
		}
		
		this.footer = footer;
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseTapStream(java.lang.String)
	 */
	public void parseTapStream( String tapStream ) throws TapParserException
	{
		Scanner scanner = null;
		
		try
		{
			scanner = new Scanner( tapStream );
			String line = null;
			
			while ( scanner.hasNextLine() )
			{
				line = scanner.nextLine();
				if ( StringUtils.isNotBlank(line) )
				{
					this.parseLine( line );
				}				
			}
			
			this.checkTAPPlanPosition();
		} 
		catch ( Exception e )
		{
			throw new TapParserException( "Error parsing TAP Stream: " + e.getMessage(), e );
		}
		finally 
		{
			if ( scanner != null )
			{
				scanner.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseFile(java.io.File)
	 */
	public void parseFile( File tapFile ) throws TapParserException
	{
		Scanner scanner = null;
		
		try
		{
			scanner = new Scanner( tapFile );
			String line = null;
			
			while ( scanner.hasNextLine() )
			{
				line = scanner.nextLine();
				if ( StringUtils.isNotBlank(line) )
				{
					this.parseLine( line );
				}
			}
			
			this.checkTAPPlanPosition();
		} 
		catch ( Exception e )
		{
			throw new TapParserException( "Error parsing TAP Stream: " + e.getMessage(), e );
		}
		finally 
		{
			if ( scanner != null )
			{
				scanner.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printSummary(java.io.PrintWriter)
	 */
	public void printSummary( PrintWriter pw )
	{
		TestSet testSet = this.getTestSet();
		String summary = testSet.getSummary();
		pw.println( summary );
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#printDetails(java.io.PrintWriter)
	 */
	public void printDetails( PrintWriter pw )
	{
		TestSet testSet = this.getTestSet();
		if ( testSet.getHeader() != null )
		{
			pw.println( testSet.getHeader().toString() );
		}
		if ( testSet.getPlan() != null )
		{
			pw.println( testSet.getPlan().toString() );
		}
		List<TapResult> tapLines = testSet.getTapLines();
		for ( TapResult tapLine :  tapLines)
		{
			pw.println( tapLine.toString() );
		}
		if ( testSet.getFooter() != null )
		{
			pw.println( testSet.getFooter().toString() );
		}
	}

}
