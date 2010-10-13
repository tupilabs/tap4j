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
import java.io.PrintWriter;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

import br.eti.kinoshita.tap4j.model.TapElement;
import br.eti.kinoshita.tap4j.model.TapResult;

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

	/**
	 * YAML parser and emitter.
	 */
	protected Yaml yaml;
	
	/**
	 * Default constructor.
	 */
	public DefaultTapProducer()
	{
		super();
		
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN); 
		options.setLineBreak(LineBreak.getPlatformLineBreak());
		options.setExplicitStart( true );
		options.setExplicitEnd( true );
		options.setIndent(10);
		// TBD: set indent is not working on yaml, perhaps we should implement 
		// a representer...
		yaml = new Yaml( options );
	}

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.TapProducer#printTo(java.io.File)
	 */
	public void printTo(File file) 
	throws IOException
	{
		PrintWriter writer = null;		
		try 
		{
			writer = new PrintWriter( file );
			
			this.printTo( writer );
		} 
		finally 
		{
			if ( writer != null )
			{
				writer.close();
			}
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
			this.printDiagnostic( header, pw );
		}
		
		pw.println( plan.toString() );
		this.printDiagnostic( plan, pw );
		
		for( TapResult tapLine : tapLines )
		{
			pw.println( tapLine.toString() );
			this.printDiagnostic( tapLine, pw );
		}
		
		if ( footer != null )
		{
			pw.println( footer.toString() );
			this.printDiagnostic( footer, pw );
		}
	}

	/**
	 * Prints diagnostic of the TAP Element into the Print Writer.
	 * 
	 * @param tapElement TAP element
	 * @param pw PrintWriter
	 */
	protected void printDiagnostic( TapElement tapElement, PrintWriter pw )
	{
		Map<String, Object> diagnostic = tapElement.getDiagnostic();
		if ( diagnostic != null )
		{
			String diagnosticText = this.yaml.dump( diagnostic );
			diagnosticText = diagnosticText.replaceAll("((?m)^)", "  ");
			pw.print( diagnosticText );
		}
	}
	
}
