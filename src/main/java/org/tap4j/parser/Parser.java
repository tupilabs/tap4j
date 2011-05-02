/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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
package org.tap4j.parser;

import java.io.File;
import java.util.regex.Pattern;

import org.tap4j.model.TestSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public interface Parser 
{

	/* -- Regular expressions -- */
	
	/**
	 * TAP Header Regex.
	 */
	String REGEX_HEADER = "\\s*TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";
	
	/**
	 * TAP Plan Regex.
	 */
	String REGEX_PLAN = "\\s*(\\d)+(\\.{2})(\\d)+\\s*(skip\\s*([^#]+))?\\s*(#\\s*(.*))?";
	
	/**
	 * TAP Test Result Regex.
	 */
	String REGEX_TEST_RESULT = "\\s*(ok|not ok)\\s*(\\d+)?\\s*([^#]*)?\\s*(#\\s*(SKIP|TODO)\\s*([^#]+))?\\s*(#\\s*(.*))?"; 
	
	/**
	 * TAP Bail Out! Regex.
	 */
	String REGEX_BAIL_OUT = "\\s*Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";
	
	/**
	 * TAP Comment Regex.
	 */
	String REGEX_COMMENT = "\\s*#\\s*(.*)";

	/**
	 * TAP Footer Regex.
	 */
	String REGEX_FOOTER = "\\s*TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";
	
	/* -- Patterns -- */
	
	/**
	 * TAP Header Regex Pattern.
	 */
	Pattern HEADER_PATTERN = Pattern.compile( REGEX_HEADER );
	
	/**
	 * TAP Plan Regex Pattern.
	 */
	Pattern PLAN_PATTERN = Pattern.compile( REGEX_PLAN );
	
	/**
	 * TAP Test Result Regex Pattern.
	 */
	Pattern TEST_RESULT_PATTERN = Pattern.compile( REGEX_TEST_RESULT );
	
	/**
	 * TAP Bail Out! Regex Pattern.
	 */
	Pattern BAIL_OUT_PATTERN = Pattern.compile ( REGEX_BAIL_OUT );
	
	/**
	 * TAP Comment Regex Pattern.
	 */
	Pattern COMMENT_PATTERN = Pattern.compile ( REGEX_COMMENT );
	
	/**
	 * TAP Footer Regex Pattern.
	 */
	Pattern FOOTER_PATTERN = Pattern.compile ( REGEX_FOOTER );
	
	/**
	 * Parses a Test Result.
	 * 
	 * @param tapLine TAP line
	 */
	void parseLine( String tapLine ) 
	throws ParserException;
	
	/**
	 * Parses a TAP Stream.
	 * 
	 * @param tapStream TAP Stream
	 */
	TestSet parseTapStream( String tapStream ) 
	throws ParserException;
	
	/**
	 * Parses a TAP File.
	 * 
	 * @param tapFile TAP File
	 */
	TestSet parseFile( File tapFile ) 
	throws ParserException;
	
}
