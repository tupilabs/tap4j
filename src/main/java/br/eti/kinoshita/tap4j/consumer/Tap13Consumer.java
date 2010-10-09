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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.TapElement;
import br.eti.kinoshita.tap4j.model.Text;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Tap13Consumer 
extends DefaultTapConsumer 
{

	protected TapElement lastParsedElement = null;
	
	/**
	 * Indicator of the base indentation level. Usually defined by the TAP 
	 * Header. 
	 */
	protected int baseIndentationLevel = -1;
	
	/**
	 * Helper indicator of in what indentantion level we are working at moment.
	 * It is helpful specially when you have many nested elements, like a META 
	 * element with some multiline text.
	 */
	protected int currentIndentationLevel = -1;
	
	protected Pattern indentationREGEX = Pattern.compile( "((\\s|\\t)*)?.*" );
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.DefaultTapConsumer#parseLine(java.lang.String)
	 */
	@Override
	public void parseLine(String tapLine) 
	throws TapParserException 
	{

		if ( StringUtils.isEmpty( tapLine ) || StringUtils.isWhitespace( tapLine ) )
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
		
		// Check if we already know the indentation level... if so, try to find 
		// out the indentation level of the current line in the TAP Stream. 
		// If the line indentation level is greater than the pre-defined 
		// one, than we know it is a) a META, b) 
		if ( this.isBaseIndentationAlreadyDefined() )
		{
			matcher = indentationREGEX.matcher( tapLine );
			if ( matcher.matches() )
			{
				String spaces = matcher.group ( 1 );
				int indentantion = spaces.length();
				if ( indentantion > this.baseIndentationLevel )
				{
					// TODO: process meta information
					System.out.println( "Meta: " + tapLine );
					return;
				}
				if ( indentantion < this.baseIndentationLevel )
				{
					throw new TapParserException("Invalid indentantion. Check your TAP Stream. Line: " + tapLine);
				}
			}
		}
		
		// Header 
		matcher = headerREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			Matcher indentMatcher = indentationREGEX.matcher( tapLine );
			if ( indentMatcher.matches() )
			{
				String spaces = indentMatcher.group ( 1 );
				this.baseIndentationLevel = spaces.length();
			} 
			else 
			{
				this.baseIndentationLevel = 0;
			}
			
			this.checkTAPHeaderParsingLocationAndDuplicity();
			
			this.extractHeader ( matcher );
			this.isFirstLine = false;
			
			this.lastParsedElement = this.header;
			
			return;
		}
		
		if ( this.header == null )
		{
			throw new TapParserException("Missing required TAP Header element.");
		}
		
		// Plan 
		matcher = planREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			
			this.checkTAPPlanDuplicity();
			
			this.checkIfTAPPlanIsSetBeforeTestResultsOrBailOut();
			
			this.extractPlan ( matcher);
			this.isFirstLine = false;
			
			this.lastParsedElement = this.plan;
			
			return;
		}
		
		// Test Result
		matcher = testResultREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractTestResult ( matcher );
			
			this.lastParsedElement = this.tapLines.get( (tapLines.size()-1) );
			
			return;
		}
		
		// Bail Out
		matcher = bailOutREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractBailOut( matcher );
			
			this.lastParsedElement = this.tapLines.get( (tapLines.size()-1) );
			
			return;
		}
		
		// Footer
		matcher = footerREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.extractFooter( matcher );
			
			this.lastParsedElement = this.footer;
			
			return;
		}
		
		// Any text. It should not be parsed by the consumer.
		final Text text = new Text( tapLine );
		this.lastParsedElement = text;
		this.tapLines.add( text );

	}
	
	protected boolean isBaseIndentationAlreadyDefined()
	{
		return this.baseIndentationLevel >= 0;
	}
	
}
