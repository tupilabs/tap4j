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

import java.io.File;
import java.util.Map;

import org.tap4j.model.TestSet;
import org.tap4j.parser.Tap13YamlParser;
import org.tap4j.util.StatusValues;
import org.tap4j.util.TapVersionValues;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTap13YamlConsumer 
{

	protected TapConsumer consumer;
	
	@BeforeMethod
	public void setUp()
	{
		consumer = new TapConsumerImpl(new Tap13YamlParser());
	}
	
	@Test
	public void test1()
	{
		try 
		{
			TestSet testSet = consumer.load( new File(TestTap13YamlConsumer.class.getResource("/input_tap13/1.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getHeader().getVersion() );
			
			Assert.assertTrue ( testSet.getHeader().getVersion().equals( TapVersionValues.TAP_13.getValue() ) );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue ( testSet.getPlan().getInitialTestNumber() == 1 );
			
			Assert.assertTrue( testSet.getPlan().getLastTestNumber() == 3 );
			
			Assert.assertTrue( testSet.getTestResults().size() == 3 );
			
			Assert.assertTrue ( testSet.getTestResult(1).getStatus() == StatusValues.OK );
			
			Assert.assertNotNull ( testSet.getTestResult(1).getDiagnostic() );
			
			Assert.assertTrue ( testSet.getTestResult(2).getStatus() == StatusValues.NOT_OK );
			
			Map<String, Object> diagnostic = testSet.getTestResult(2).getDiagnostic();
			
			Assert.assertNotNull( diagnostic );
			
			Assert.assertEquals( diagnostic.get("file"), "t/something.t" );
			
			Assert.assertTrue ( testSet.getTestResult(3).getStatus() == StatusValues.OK );
			
		} 
		catch (TapConsumerException e) 
		{
			Assert.fail( e.getMessage());
		}
	}
	
	@Test
	public void test2()
	{
		try 
		{
			TestSet testSet = consumer.load( new File(TestTap13YamlConsumer.class.getResource("/input_tap13/org.tap4j.testng.TestGoogleBrunoKinoshita.tap").getFile()) );
			
			Assert.assertNotNull( testSet.getHeader() );
			
			Assert.assertNotNull( testSet.getHeader().getVersion() );
			
			Assert.assertTrue ( testSet.getHeader().getVersion().equals( TapVersionValues.TAP_13.getValue() ) );
			
			Assert.assertNotNull( testSet.getPlan() );
			
			Assert.assertTrue ( testSet.getPlan().getInitialTestNumber() == 1 );
			
			Assert.assertTrue( testSet.getPlan().getLastTestNumber() == 1 );
			
			Assert.assertTrue( testSet.getTestResults().size() == 1 );
			
			Assert.assertTrue ( testSet.getTestResult(1).getStatus() == StatusValues.NOT_OK );
			
			Assert.assertNotNull ( testSet.getTestResult(1).getDiagnostic() );
			
			Map<String, Object> diagnostic = testSet.getTestResult(1).getDiagnostic();
			
			Assert.assertNotNull( diagnostic );
			
			Assert.assertEquals( diagnostic.get("file"), "org.tap4j.testng.TestGoogleBrunoKinoshita.java" );
			
		} 
		catch (TapConsumerException e) 
		{
			Assert.fail( e.getMessage());
		}
	}
	
}
