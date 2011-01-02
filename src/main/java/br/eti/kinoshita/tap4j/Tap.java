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
package br.eti.kinoshita.tap4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import br.eti.kinoshita.tap4j.consumer.DefaultTapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapParserException;
import br.eti.kinoshita.tap4j.producer.DefaultTapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducer;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.4
 */
public class Tap 
{
	
	protected TapConsumer consumer;
	protected TapProducer producer;

	public Tap()
	{
		this.consumer = new DefaultTapConsumer();
		this.producer = new DefaultTapProducer();
	}
	
	public Tap(TapProducer producer)
	{
		this.consumer = new DefaultTapConsumer();
		this.producer = producer;
	}
	
	public Tap(TapConsumer consumer)
	{
		this.consumer = consumer;
		this.producer = new DefaultTapProducer();
	}
	
	public Tap(TapConsumer consumer, TapProducer producer)
	{
		this.producer = producer;
	}
	
	public void load( File file ) 
	throws TapParserException
	{
		this.consumer.parseFile( file );
	}
	
	public void load( String stream ) 
	throws TapParserException
	{
		this.consumer.parseTapStream( stream );
	}
	
	public void dump( File file ) 
	throws TapParserException
	{
		try 
		{
			this.producer.printTo( file );
		} 
		catch (IOException e) 
		{
			throw new TapParserException(e);
		}
	}
	
	public void dump( PrintWriter pw ) 
	throws TapParserException
	{
		try 
		{
			this.producer.printTo( pw );
		} 
		catch (Throwable e) 
		{
			throw new TapParserException(e);
		}
	}
	
	public String dump() 
	throws TapParserException
	{
		try 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			this.producer.printTo( pw );
			return sw.toString();
		} 
		catch (Throwable e) 
		{
			throw new TapParserException(e);
		}
	}
	
}
