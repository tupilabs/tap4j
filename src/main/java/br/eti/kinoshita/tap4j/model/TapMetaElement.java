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
package br.eti.kinoshita.tap4j.model;

import java.util.LinkedHashMap;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public abstract class TapMetaElement 
implements TapElement
{
	
	/**
	 * Tap Element meta map.
	 */
	protected final LinkedHashMap<String, Object> meta = new LinkedHashMap<String, Object>();

	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.model.TapElement#addMetaInformation(java.lang.String, java.lang.Object)
	 */
	public void addMetaInformation( String key, Object value )
	{
		if ( key != null )
		{
			key = key.trim();
		}
		if ( value != null )
		{
			if ( value instanceof String )
			{
				value = ((String)value).trim();
			}
		}
		this.meta.put( key, value );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.model.TapElement#getMetaInformation(java.lang.String)
	 */
	public Object getMetaInformation( String key )
	{
		return this.meta.get( key );
	}
	
	/* (non-Javadoc)
	 * @see br.eti.kinoshita.tap4j.model.TapElement#getMeta()
	 */
	public LinkedHashMap<String, Object> getMeta()
	{
		return this.meta;
	}
	
}
