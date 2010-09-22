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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class DefaultTapProducer 
extends AbstractTapProducer
{

	public DefaultTapProducer(Integer tapVersion)
	{
		super( tapVersion );
	}
	
	public void addText( String text )
	{
		this.testResultsList.add( text );
	}
	
	/**
	 * Prints TAP Stream to a PrintStream
	 * 
	 * @param ps Print Stream
	 */
	public void printTo( PrintStream ps )
	{
		ps.print( this.getTapStream() );
	}
	
	/**
	 * Prints TAP Stream to a PrintWriter
	 * 
	 * @param pw Print Writer
	 */
	public void printTo( PrintWriter pw )
	{
		pw.write( this.getTapStream() );
	}
	
	/**
	 * Prints TAP Stream to a File
	 * 
	 * @param output Output file
	 * @throws IOException
	 */
	public void printTo( File output ) 
	throws IOException
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter( output );
			this.printTo( writer );
		} 
		finally 
		{
			if ( writer != null )
			{
				writer.flush();
				writer.close();
			}
		}
	}
	
}
