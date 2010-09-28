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
 * Default implementation of a TAP Producer. This class implements only methods 
 * associated to printing the TAP Stream into some kind of media. The rest of 
 * the methods are handled by the Abstract TAP Producer.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 * @see {@link AbstractTapProducer}
 */
public class DefaultTapProducer 
extends AbstractTapProducer
{

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#printTo(java.io.PrintStream)
	 */
	public void printTo(PrintStream ps) 
	{
		if ( header != null )
		{
			ps.println( header.toString() );
		}
		
		ps.println( plan.toString() );
		
		for( TapLine tapLine : tapLines )
		{
			ps.println( tapLine.toString() );
		}
		
		if ( footer != null )
		{
			ps.println( footer.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#printTo(java.io.PrintWriter)
	 */
	public void printTo(PrintWriter pw) 
	{
		if ( header != null )
		{
			pw.println( header.toString() );
		}
		
		pw.println( plan.toString() );
		
		for( TapLine tapLine : tapLines )
		{
			pw.println( tapLine.toString() );
		}
		
		if ( footer != null )
		{
			pw.println( footer.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#printTo(java.io.File)
	 */
	public void printTo(File file) 
	throws IOException
	{
		PrintStream writer = null;
		
		try 
		{
			writer = new PrintStream( file );
			
			if ( header != null )
			{
				writer.println( header.toString() );
			}
			
			writer.println( plan.toString() );
			
			for( TapLine tapLine : tapLines )
			{
				writer.println( tapLine.toString() );
			}
			
			if ( footer != null )
			{
				writer.println( footer.toString() );
			}
		} 
		finally 
		{
			if ( writer != null )
			{
				writer.close();
			}
		}
		
	}
	
}
