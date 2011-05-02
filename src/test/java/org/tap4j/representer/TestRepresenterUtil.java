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
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

import org.tap4j.model.TestResult;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestRepresenterUtil
{

	@Test
	public void testRepresenterUtil()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Yaml yaml = new Yaml();
		TestResult tr = new TestResult();
		
		tr.setDiagnostic( null );
		
		RepresenterUtil.printDiagnostic(yaml, tr, pw);
		
		Assert.assertNotNull( sw.toString() );
		
		tr.setDiagnostic(new LinkedHashMap<String, Object>());
		
		Assert.assertNotNull( sw.toString() );
	}
	
	@Test
	public void testRepresenterUtilConstructor()
	{
		try
		{
			final Constructor<?> c = RepresenterUtil.class.getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			Assert.assertNotNull(o);
		}
		catch (Exception e)
		{
			Assert.fail("Failed to instantiate RepresenterUtil constructor: " + e.getMessage(), e);
		}
	}
	
}
