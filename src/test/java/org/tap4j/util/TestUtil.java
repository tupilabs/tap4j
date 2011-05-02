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
package org.tap4j.util;

import java.lang.reflect.Constructor;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestUtil 
{

	private StringBuilder sb;

	@BeforeMethod
	public void setUp()
	{
		sb = new StringBuilder();
		sb.append("Anything");
	}

	@Test
	public void testAppends()
	{
		Util.appendIfNotNull(sb, " appended text");
		Assert.assertEquals(sb.toString(), "Anything appended text");

		Util.appendIfNotNull(sb, " - ", " Tom T.", null);
		Assert.assertEquals(sb.toString(), "Anything appended text -  Tom T.");
	}

	@Test
	public void testUtilConstructor()
	{
		try
		{
			final Constructor<?> c = Util.class.getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			Assert.assertNotNull(o);
		}
		catch (Exception e)
		{
			Assert.fail("Failed to instantiate Util constructor: " + e.getMessage(), e);
		}
	}

}
