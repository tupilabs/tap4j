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

import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.parser.Parser;
import br.eti.kinoshita.tap4j.parser.ParserException;
import br.eti.kinoshita.tap4j.parser.Tap13Parser;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TapConsumerImpl 
implements TapConsumer
{
	
	private Parser parser;
	private TestSet testSet;
	
	public TapConsumerImpl()
	{
		parser = new Tap13Parser();
		testSet = new TestSet();
	}
	
	public TapConsumerImpl(Parser parser)
	{
		this.parser = parser;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.TapConsumer#getTestSet()
	 */
	public TestSet getTestSet() 
	{
		return this.testSet;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.TapConsumer#load(java.io.File)
	 */
	public TestSet load(File file) 
	throws TapConsumerException 
	{
		try 
		{
			this.testSet = this.parser.parseFile( file );
		} 
		catch (ParserException e)
		{
			throw new TapConsumerException(
					"Failed to parse file " + file + ": " + e.getMessage(), e);
		}
		
		return this.testSet;
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.consumer.TapConsumer#load(java.lang.String)
	 */
	public TestSet load(String tapStream) 
	throws TapConsumerException 
	{
		try 
		{
			this.testSet = this.parser.parseTapStream( tapStream );
		} 
		catch (ParserException e)
		{
			throw new TapConsumerException(
					"Failed to parse TAP Stream " + tapStream + ": " + e.getMessage(), e);
		}
		
		return this.testSet;
	}
	
}
