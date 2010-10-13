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
package br.eti.kinoshita.tap4j.util;



/**
 * Utility Class.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Util
{
	
	/**
	 * Hiding the constructor of the utility class.
	 */
	private Util(){}
	
	/**
	 * Appends a text to the StringBuffer with a prefix and suffix.
	 * 
	 * @param sb
	 * @param prefix
	 * @param object
	 * @param suffix
	 */
	public static void appendIfNotNull( StringBuffer sb, String prefix, Object object, String suffix )
	{
		if ( object != null )
		{
			appendIfNotNull( sb, prefix );
			
			sb.append ( object.toString() );
			
			appendIfNotNull( sb, suffix );
		}
	}
	
	/**
	 * Appends a text to a given StringBuffer if the text is not null.
	 * 
	 * @param sb StringBuffer object.
	 * @param text String text to be appended.
	 */
	public static void appendIfNotNull ( StringBuffer sb, String text )
	{
		if ( text != null )
		{
			sb.append ( text );
		}
	}

}
