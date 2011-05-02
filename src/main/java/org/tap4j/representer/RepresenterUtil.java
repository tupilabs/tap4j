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
import java.util.Map;

import org.tap4j.model.TapElement;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public final class RepresenterUtil 
{

	private RepresenterUtil()
	{
		super();
	}
	
	/**
	 * Prints diagnostic of the TAP Element into the Print Writer.
	 * 
	 * @param yaml YAML instance to dump YAML documents
	 * @param tapElement TAP element
	 * @param pw PrintWriter
	 */
	protected static void printDiagnostic( Yaml yaml, TapElement tapElement, PrintWriter pw )
	{
		Map<String, Object> diagnostic = tapElement.getDiagnostic();
		if ( diagnostic != null && !diagnostic.isEmpty() )
		{
			String diagnosticText = yaml.dump( diagnostic );
			diagnosticText = diagnosticText.replaceAll("((?m)^)", "  ");
			pw.print( diagnosticText );
		}
	}
	
}
