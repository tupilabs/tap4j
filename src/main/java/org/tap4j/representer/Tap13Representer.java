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
package org.tap4j.representer;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.tap4j.model.TapResult;
import org.tap4j.model.TestSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Tap13Representer 
implements Representer
{

	/* (non-Javadoc)
	 * @see org.tap4j.representer.Representer#representData(org.tap4j.model.TestSet)
	 */
	public String representData(TestSet testSet) 
	throws RepresenterException
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		if ( testSet.getHeader() != null )
		{
			pw.println( testSet.getHeader().toString() );
		}
		
		if ( testSet.getPlan() == null )
		{
			throw new RepresenterException("Missing required TAP Plan");
		}
		
		pw.println( testSet.getPlan().toString() );
		
		for( TapResult tapLine : testSet.getTapLines() )
		{
			pw.println( tapLine.toString() );
		}
		
		if ( testSet.getFooter() != null )
		{
			pw.println( testSet.getFooter().toString() );
		}
		return sw.toString();
	}

}
