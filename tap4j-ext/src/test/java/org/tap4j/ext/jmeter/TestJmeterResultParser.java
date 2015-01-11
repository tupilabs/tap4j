/*
 * The MIT License
 * 
 * Copyright (c) 2010 tap4j team (see AUTHORS)
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

package org.tap4j.ext.jmeter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.ext.jmeter.parser.JmeterResultParser;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests JmeterResultParser class.
 * 
 * @author s2o
 */
public class TestJmeterResultParser {

	private static final String UTF_8 = "UTF-8";
	private static final String PATH_FILES = "/org/tap4j/ext/jmeter/";

	@DataProvider(name = "testParseJmeter")
	public Object[][] files() {
		return new Object[][] {
				{ "SI.MCA.ASECheckList-all.xml", "expected/SI.MCA.ASECheckList-all.tap" },
				{ "jmeter-template.xml", "expected/jmeter-template.tap" } };
	}

	//
	@Test(dataProvider = "testParseJmeter")
	public void testGetMessage(String filename, String tapFileNameOk) throws URISyntaxException {

		URL resourceUrl = getClass().getResource(PATH_FILES + filename);
		File jMeterFile = new File(resourceUrl.toURI());

		JmeterResultParser jmParse = new JmeterResultParser(Charset.forName(UTF_8));
		TestSet testResult = jmParse.parseFile(jMeterFile, true);
		Producer tapProducer = TapProducerFactory.makeTap13YamlProducer();
		String tapActualDump = tapProducer.dump(testResult);

		resourceUrl = getClass().getResource(PATH_FILES + tapFileNameOk);
		File tapFileOk = new File(resourceUrl.toURI());
		TestSet tapOk = TapConsumerFactory.makeTap13YamlConsumer().load(tapFileOk);
		tapProducer = TapProducerFactory.makeTap13YamlProducer();
		String tapOkDump = tapProducer.dump(tapOk);

		Assert.assertEquals(tapOkDump, tapActualDump);
	}


}
