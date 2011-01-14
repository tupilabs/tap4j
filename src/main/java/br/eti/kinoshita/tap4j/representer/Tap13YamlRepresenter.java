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
package br.eti.kinoshita.tap4j.representer;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

import br.eti.kinoshita.tap4j.model.TapResult;
import br.eti.kinoshita.tap4j.model.TestSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Tap13YamlRepresenter 
implements Representer
{

	/**
	 * YAML parser and emitter.
	 */
	protected Yaml yaml;
	
	/**
	 * Default constructor.
	 */
	public Tap13YamlRepresenter()
	{
		super();
		
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		//options.setDefaultScalarStyle(DumperOptions.ScalarStyle.LITERAL); 
		options.setLineBreak(LineBreak.getPlatformLineBreak());
		options.setExplicitStart( true );
		options.setExplicitEnd( true );
		// TBD: set indent is not working on yaml, perhaps we should implement 
		// a representer...
		yaml = new Yaml( options );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.representer.Representer#representData(br.eti.kinoshita.tap4j.model.TestSet)
	 */
	public String representData(TestSet testSet) 
	throws RepresenterException
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		if ( testSet.getHeader() != null )
		{
			pw.println( testSet.getHeader().toString() );
			RepresenterUtil.printDiagnostic( yaml, testSet.getHeader(), pw );
		}
		
		if ( testSet.getPlan() == null )
		{
			throw new RepresenterException("Missing required TAP Plan");
		}
		
		pw.println( testSet.getPlan().toString() );
		RepresenterUtil.printDiagnostic( yaml, testSet.getPlan(), pw );
		
		for( TapResult tapLine : testSet.getTapLines() )
		{
			pw.println( tapLine.toString() );
			RepresenterUtil.printDiagnostic( yaml,tapLine, pw );
		}
		
		if ( testSet.getFooter() != null )
		{
			pw.println( testSet.getFooter().toString() );
			RepresenterUtil.printDiagnostic( yaml, testSet.getFooter(), pw );
		}
		return sw.toString();
	}

}
