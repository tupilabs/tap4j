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
package br.eti.kinoshita.tap4j.producer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import br.eti.kinoshita.tap4j.model.BailOut;
import br.eti.kinoshita.tap4j.model.Comment;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TapResult;
import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.TestResult;

/**
 * Produces a TAP Stream.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public interface TapProducer
{
	
	/* -- Header -- */
	/**
	 * @param header Header.
	 */
	public void setHeader( Header header );
	
	/* -- Plan -- */
	/**
	 * @param plan Plan.
	 */
	public void setPlan( Plan plan );
	
	/* -- Test Results and Bail Outs -- */
	
	/**
	 * @param testResult Test Result.
	 */
	public boolean addTestResult( TestResult testResult );
	
	/**
	 * @param bailOut Bail Out.
	 */
	public boolean addBailOut( BailOut bailOut );
	
	/**
	 * @return List of TAP Lines.
	 */
	public List<TapResult> getTapLines();
	
	/* -- Comment -- */
	/**
	 * @param comment Comment.
	 */
	public boolean addComment( Comment comment );

	/* -- Footer -- */
	/**
	 * @param footer Footer.
	 */
	public void addFooter( Footer footer );
	
	/* -- Printing the TAP Stream -- */
	
	/**
	 * Prints the TAP Stream into a Print Stream.
	 * 
	 * @param ps The Print Stream.
	 */
	public void printTo( PrintStream ps );
	
	/**
	 * Prints the TAP Stream into a Print Writer.
	 * 
	 * @param pw The Print Writer.
	 */
	public void printTo( PrintWriter pw );
	
	/**
	 * Prints a TAP Stream into a File.
	 * 
	 * @param file File to print the TAP Stream into it.
	 */
	public void printTo( File file ) 
	throws IOException;

}
