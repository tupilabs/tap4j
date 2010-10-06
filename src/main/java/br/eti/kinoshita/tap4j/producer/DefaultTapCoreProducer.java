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

import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TapResult;
import br.eti.kinoshita.tap4j.model.TestResult;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class DefaultTapCoreProducer 
extends DefaultTapProducer
{

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#printTo(java.io.PrintWriter)
	 */
	public void printTo(PrintWriter pw) 
	{
		// Ignore header
//		if ( header != null )
//		{
//			ps.println( header.toString() );
//		}
		
		//ps.println( plan.toString() );
		Plan plan = new Plan( this.numberOfBailOuts + this.numberOfTestResults );
		pw.println( plan );
		
		int index = 1;
		for( TapResult tapLine : tapLines )
		{
			if ( tapLine instanceof TestResult )
			{
				TestResult testResult = (TestResult) tapLine ;
				pw.print( testResult.getStatus() );
				pw.print( " " + index );
				if ( ! StringUtils.isEmpty( testResult.getDescription() ) ) 
				{
					pw.print(" " + testResult.getDescription());
				}
				if ( testResult.getDirective() != null )
				{
					pw.print(" " + testResult.getDirective().toString());
				}
				pw.println();
				index += 1;
			}
			else
			{
				pw.println( tapLine );
			}
		}
		
		// Ignore footer
//		if ( footer != null )
//		{
//			ps.println( footer.toString() );
//		}
	}
	
}
