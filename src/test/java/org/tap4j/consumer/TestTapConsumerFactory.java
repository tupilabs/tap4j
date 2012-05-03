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

import java.lang.reflect.Constructor;

import org.tap4j.parser.Parser;
import org.tap4j.parser.Tap13Parser;
import org.tap4j.parser.Tap13YamlParser;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for TAP Consumer Factory.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTapConsumerFactory {

	@Test
	public void testMakeTap13Consumer() {
		TapConsumer tapConsumer = TapConsumerFactory.makeTap13Consumer();

		Parser tap13Parser = tapConsumer.getParser();

		Assert.assertTrue(tap13Parser instanceof Tap13Parser);
	}

	@Test
	public void testMakeTap13YamlConsumer() {
		TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();

		Parser tap13YamlParser = tapConsumer.getParser();

		Assert.assertTrue(tap13YamlParser instanceof Tap13YamlParser);
	}

	@Test
	public void testTapConsumerFactoryConstructor() {
		try {
			final Constructor<?> c = TapConsumerFactory.class
			        .getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			Assert.assertNotNull(o);
		} catch (Exception e) {
			Assert.fail(
			        "Failed to instantiate TapConsumerFactory constructor: "
			                + e.getMessage(), e);
		}
	}

}
