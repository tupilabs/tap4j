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

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Util
{
	
	private static final String STATUS_OK = "ok";
	private static final String STATUS_NOT_OK = "not ok";
	private static final String DIRECTIVE_SKIP = "SKIP";
	private static final String DIRECTIVE_TODO = "TODO";
	
	public static String getStatusText( Status status )
	{
		if ( status.equals( Status.OK ) )
		{
			return STATUS_OK;
		} 
		else			
		{
			return STATUS_NOT_OK;
		}
	}
	
	public static String getDirectiveText( Directive directive )
	{
		if ( directive.equals( Directive.SKIP ) )
		{
			return DIRECTIVE_SKIP;
		} 
		else			
		{
			return DIRECTIVE_TODO;
		}
	}

}
