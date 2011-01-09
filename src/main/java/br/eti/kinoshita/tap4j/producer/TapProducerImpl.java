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
package br.eti.kinoshita.tap4j.producer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;

import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.representer.Representer;
import br.eti.kinoshita.tap4j.representer.Tap13Representer;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TapProducerImpl 
implements TapProducer 
{

	protected Representer representer;
	
	public TapProducerImpl()
	{
		super();
		representer = new Tap13Representer();
	}
	
	public TapProducerImpl(Representer representer)
	{
		this.representer = new Tap13Representer();
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.producer.TapProducer#dump(br.eti.kinoshita.tap4j.model.TestSet)
	 */
	public String dump(TestSet testSet) 
	throws TapProducerException 
	{
		return this.representer.representData(testSet);		
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.producer.TapProducer#dump(br.eti.kinoshita.tap4j.model.TestSet, java.io.Writer)
	 */
	public void dump(TestSet testSet, Writer writer)
	throws TapProducerException 
	{
		String tapStream = this.dump(testSet);
		try 
		{
			writer.append(tapStream);
		} 
		catch (IOException e) 
		{
			throw new TapProducerException("Failed to dump TAP Stream: " + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.producer.TapProducer#dump(br.eti.kinoshita.tap4j.model.TestSet, java.io.File)
	 */
	public void dump(TestSet testSet, File output)
	throws TapProducerException 
	{
		String tapStream = this.dump(testSet);
		
		try 
		{
			FileUtils.writeStringToFile(output, tapStream);
		}
		catch (IOException e) 
		{
			throw new TapProducerException("Failed to dump TAP Stream: " + e.getMessage(), e);
		}
	}

}
