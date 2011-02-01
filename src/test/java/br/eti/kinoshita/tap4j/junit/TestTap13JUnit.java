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
package br.eti.kinoshita.tap4j.junit;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import br.eti.kinoshita.tap4j.model.Comment;
import br.eti.kinoshita.tap4j.model.Footer;
import br.eti.kinoshita.tap4j.model.Header;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestResult;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.producer.TapProducer;
import br.eti.kinoshita.tap4j.producer.TapProducerFactory;
import br.eti.kinoshita.tap4j.producer.TapProducerImpl;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * 
 * @author cesar.almeida
 */
public class TestTap13JUnit
{
	private static final Integer TAP_VERSION = 13;
	private TapProducer tapProducer;
	private TestSet testSet;
	
	// Temp file to where we output the generated tap stream.
	private File tempFile;
	
	private static final Integer INITIAL_TEST_STEP = 1;
	
	@Test
	public void testTapProducer()
	{
		tapProducer = new TapProducerImpl();
		testSet = new TestSet();
		Header header = new Header( TAP_VERSION );
		testSet.setHeader(header);
		Plan plan = new Plan(INITIAL_TEST_STEP, 3);
		testSet.setPlan(plan);
		Comment singleComment = new Comment( "Starting tests" );
		testSet.addComment( singleComment );
		
		TestResult tr1 = new TestResult(StatusValues.OK, 1);
		testSet.addTestResult(tr1);
		
		TestResult tr2 = new TestResult(StatusValues.NOT_OK, 2);
		tr2.setTestNumber(2);
		testSet.addTestResult(tr2);
		
		testSet.setFooter( new Footer("End") );
		
		try
		{
			tempFile = File.createTempFile("tap4j_junit_", ".tap");
		} catch (IOException e)
		{
			Assert.fail("Failed to create temp file: " + e.getMessage());
		}
		
		
		Assert.assertTrue ( testSet.getTapLines().size() > 0 );
		
		Assert.assertTrue ( false );
		
		try
		{
			tapProducer.dump( testSet, tempFile );
		}
		catch ( Exception e  )
		{
			Assert.fail("Failed to print TAP Stream into file.");
		}
	}
	
	@Test
	public void testSingleTestResultWithoutTestNumber()
	{
		TapProducer tapProducer = TapProducerFactory.makeTap13Producer();
		TestSet testSet = new TestSet();
		Plan plan = new Plan(1,1);
		testSet.setPlan(plan);
		TestResult okTestResult = new TestResult();
		okTestResult.setStatus(StatusValues.OK);
		testSet.addTestResult( okTestResult );
		String output = tapProducer.dump(testSet);
		Assert.assertFalse( output.contains("-1") );
	}

	@Test @Ignore
	public void testIgnored()
	{
		Assert.assertFalse( true );
	}
}
