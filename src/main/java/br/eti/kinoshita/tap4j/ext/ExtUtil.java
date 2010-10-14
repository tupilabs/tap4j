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
package br.eti.kinoshita.tap4j.ext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class ExtUtil
{
	
	// REGEX
	protected static Pattern wantedREGEX = Pattern.compile(".*expected:<([^>]*)>.*");
	protected static Pattern foundREGEX = Pattern.compile(".*was:<([^>]*)>.*");
	protected static Pattern wantedExceptionREGEX = Pattern.compile(".*xpected exception (.*) but.*");
	protected static Pattern foundExceptionREGEX = Pattern.compile(".*got ([^:]*):.*");
	
	/**
	 * @param throwable Any Throwable.
	 * @return Throwable transformed into a String. 
	 */
	public static String fromThrowableToString( Throwable throwable )
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter( sw, true );
		throwable.printStackTrace( writer );
		writer.flush();
		sw.flush();
		String exceptionText = sw.toString();
		exceptionText = exceptionText.replaceAll("(\r|\n)", "");
		exceptionText = exceptionText.replaceAll("(\t)", " ");
		return exceptionText;
	}
	
	/**
	 * Retrieves a token given a regex pattern. Or null if couldn't find the 
	 * token using regex on the text.
	 * 
	 * @param text
	 * @param pattern
	 * @return REGEX Token
	 */
	public static String maybeRetrieveToken(String text, Pattern pattern)
	{
		String token = null;
		Matcher m = pattern.matcher( text );
		if ( m.matches() )
		{
			token = m.group( 1 );
		}
		return token;
	}
	
	/**
	 * @param exceptionText Exception text.
	 * @return wanted text or null if it's not present.
	 */
	public static String maybeRetrieveTestNGWanted( String exceptionText )
	{
		return maybeRetrieveToken( exceptionText, wantedREGEX );
	}
	
	/**
	 * @param exceptionText Exception text.
	 * @return found text or null if it's not present.
	 */
	public static String maybeRetrieveTestNGFound( String exceptionText )
	{
		return maybeRetrieveToken( exceptionText, foundREGEX );
	}
	
	/**
	 * @param exceptionText Exception text.
	 * @return text or null if it's not present.
	 */
	public static String maybeRetriveTestNGWantedException( String exceptionText )
	{
		return maybeRetrieveToken( exceptionText, wantedExceptionREGEX);
	}
	
	/**
	 * @param exceptionText Exception text.
	 * @return text or null if it's not present.
	 */
	public static String maybeRetriveTestNGFoundException( String exceptionText )
	{
		return maybeRetrieveToken( exceptionText, foundExceptionREGEX);
	}

}
