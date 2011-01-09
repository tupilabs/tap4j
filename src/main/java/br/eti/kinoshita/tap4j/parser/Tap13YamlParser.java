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
package br.eti.kinoshita.tap4j.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import br.eti.kinoshita.tap4j.consumer.TapConsumerException;
import br.eti.kinoshita.tap4j.model.TapElement;
import br.eti.kinoshita.tap4j.model.Text;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Tap13YamlParser 
extends Tap13Parser 
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
	
	/**
	 * YAML parser and emitter.
	 */
	protected Yaml yaml = new Yaml();
	
	protected StringBuffer diagnosticBuffer = new StringBuffer();
	
	protected Pattern indentationREGEX = Pattern.compile( "((\\s|\\t)*)?.*" );
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.DefaultTapConsumer#parseLine(java.lang.String)
	 */
	@Override
	public void parseLine(String tapLine) 
	throws ParserException 
	{
		
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
				int indentation = spaces.length();
				this.currentIndentationLevel = indentation;
				if ( indentation > this.baseIndentationLevel )
				{
					// we are at the start of the meta tags, but we should ignore 
					// the --- or ...
					// TBD: check how snakeyaml can handle these tokens.
					
					this.appendTapLineToDiagnosticBuffer( tapLine );
					
					return;
				}
				
				// indentation cannot be less then the base indentation level
				this.checkIndentationLevel( indentation, tapLine );
			}
		}
		
		// Check if we have some diagnostic set in the buffer
		this.checkAndParseTapDiagnostic();
		
		// Header 
		matcher = headerREGEX.matcher( tapLine );
		if ( matcher.matches() )
		{
			this.baseIndentationLevel = this.getIndentationLevel( tapLine );
			
			this.currentIndentationLevel = this.baseIndentationLevel;
			
			this.checkTAPHeaderParsingLocationAndDuplicity();
			
			this.extractHeader ( matcher );
			this.isFirstLine = false;
			
			this.lastParsedElement = this.header;
			
			return;
		}
		
		// Check if the header was set
		// this.checkHeader();
		
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

	/**
	 * Checks if the indentation is greater than the 
	 * {@link #baseIndentationLevel}
	 * 
	 * @param indentation indentation level
	 * @throws TapConsumerException if indentation is less then the 
	 *   {@link #baseIndentationLevel}.
	 */
	private void checkIndentationLevel( int indentation, String tapLine )
	throws ParserException
	{
		if ( indentation < this.baseIndentationLevel )
		{
			throw new ParserException("Invalid indentantion. " +
					"Check your TAP Stream. Line: " + tapLine);
		}
	}

	/**
	 * Gets the indentation level of a line.
	 * 
	 * @param tapLine line.
	 * @return indentation level of a line.
	 */
	private int getIndentationLevel( String tapLine )
	{
		int indentationLevel = 0;
		
		final Matcher indentMatcher = indentationREGEX.matcher( tapLine );
		
		if ( indentMatcher.matches() )
		{
			String spaces = indentMatcher.group ( 1 );
			indentationLevel = spaces.length();
		} 
		return indentationLevel;
	}

	/**
	 * <p>Checks if there is any diagnostic information on the diagnostic 
	 * buffer.</p>
	 * 
	 * <p>If so, tries to parse it using snakeyaml.</p>
	 * 
	 * @throws TapConsumerException
	 */
	private void checkAndParseTapDiagnostic() 
	throws ParserException
	{
		// If we found any meta, then process it with SnakeYAML
		if (  diagnosticBuffer.length() > 0 )
		{
			
			if ( this.lastParsedElement == null )
			{
				throw new ParserException("Found diagnostic information without a previous TAP element.");
			}
			
			try
			{
				//Iterable<?> metaIterable = (Iterable<?>)yaml.loadAll( diagnosticBuffer.toString() );
				@SuppressWarnings("unchecked")
				Map<String, Object> metaIterable = (Map<String, Object>)yaml.load( diagnosticBuffer.toString() );
				this.lastParsedElement.setDiagnostic( metaIterable );	
			}
			catch ( Exception ex )
			{
				throw new ParserException("Error parsing YAML ["+diagnosticBuffer.toString()+"]: " + ex.getMessage(), ex);
			}
			
			diagnosticBuffer = new StringBuffer();
		}
	}
	
	/*
	 * Checks if the Header was set.
	 * 
	 * @throws TapConsumerException
	 * @deprecated
	 */
//	void checkHeader() 
//	throws TapConsumerException
//	{
//		if ( this.header == null )
//		{
//			throw new TapConsumerException("Missing required TAP Header element.");
//		}
//	}
	
	/**
	 * Appends a diagnostic line to diagnostic buffer. If the diagnostic line 
	 * contains --- or ... then it ignores this line. In the end of each line 
	 * it appends a break line.
	 * 
	 * @param diagnosticLine diagnostic line
	 */
	private void appendTapLineToDiagnosticBuffer( String diagnosticLine )
	{
		if ( diagnosticLine.trim().equals("---") || diagnosticLine.trim().equals("...") )
		{
			return;
		}
		diagnosticBuffer.append( diagnosticLine );
		diagnosticBuffer.append( "\n" );
	}

	/**
	 * @return true if the base indentation is already defined, false  
	 * otherwise.
	 */
	protected boolean isBaseIndentationAlreadyDefined()
	{
		return this.baseIndentationLevel >= 0;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.DefaultTapConsumer#postProcess()
	 */
	@Override
	protected void postProcess() 
	throws TapConsumerException 
	{
		super.postProcess();
		this.checkAndParseTapDiagnostic();
	}
	
}
