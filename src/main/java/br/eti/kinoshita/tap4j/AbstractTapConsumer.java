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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * @since 20/09/2010
 */
public abstract class AbstractTapConsumer 
implements TapConsumer
{
	
	protected boolean headerAlreadyFound = false;
	protected boolean ignoreHeader = false;
	
	protected String header;
	
	protected int numberOfComments = 0;
	
	protected List<String> listOfComments = new ArrayList<String>();

	public void setIgnoreHeader( boolean ignoreHeader )
	{
		this.ignoreHeader = ignoreHeader; 
	}
	
	public boolean getIgnoreHeader()
	{
		return this.ignoreHeader;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseFile(java.io.File)
	 */
	public void parseFile( File tapFile ) 
	throws TapParserException
	{
		if ( tapFile == null || ! tapFile.canRead() )
		{
			throw new TapParserException("Can not read tap file " + tapFile );
		}
		
		Scanner scanner = null;
		try 
		{
			scanner = new Scanner( tapFile );
			
			while ( scanner.hasNextLine())
			{
				this.parseLine( scanner.nextLine() );
			}
		}
		catch (FileNotFoundException e) 
		{
			throw new TapParserException("Failed reading tap file: " + e.getMessage(), e);
		} 
		finally 
		{
			scanner.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapConsumer#parseLine(java.lang.String)
	 */
	public void parseLine(String testResult) 
	throws TapParserException 
	{
		
		Scanner scanner = new Scanner( testResult );
		scanner.useDelimiter(" ");
		
		if ( ! headerAlreadyFound )
		{
			if ( isComment( testResult ) )
			{
				numberOfComments += 1;
				listOfComments.add( testResult.substring(testResult.indexOf("#"), testResult.length()));
			} 
			
		}
		
	}
	
	private boolean isComment( String text )
	{
		return text != null && text.trim().startsWith("#");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
	
}
