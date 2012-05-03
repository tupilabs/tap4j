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
package org.tap4j.consumer;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for TapConsumerException.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTapConsumerExceptionTest {

	private TapConsumerException exception;

	@Test
	public void testTapConsumerException1() {
		exception = new TapConsumerException();

		Assert.assertNotNull(exception);
	}

	@Test
	public void testTapConsumerException2() {
		exception = new TapConsumerException("Error parsing document");

		Assert.assertNotNull(exception);

		Assert.assertEquals(exception.getMessage(), "Error parsing document");
	}

	@Test
	public void testTapConsumerException3() {
		exception = new TapConsumerException(new NullPointerException(
		        "Null TAP Stream")); // NOPMD

		Assert.assertNotNull(exception);

		Assert.assertTrue(exception.getCause() instanceof NullPointerException);
	}

	@Test
	public void testTapConsumerException4() {
		exception = new TapConsumerException("Null", new NullPointerException()); // NOPMD

		Assert.assertNotNull(exception);

		Assert.assertEquals(exception.getMessage(), "Null");

		Assert.assertTrue(exception.getCause() instanceof NullPointerException);
	}

}
