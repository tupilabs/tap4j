/*
 * The MIT License
 *
 * Copyright (c) <2011> <tap4j>
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
package org.tap4j.producer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.tap4j.ext.testng.TestTAPReporter;
import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.inject.Inject;

/**
 * This class uses the awesome script created by Patrick LeBoutillier to test
 * TAP Streams generated with tap4j using Perl Test::Harness. This test is an
 * integration test, so by default it's disabled. In order to run this test,
 * execute mvn test -DintegrationTests=true.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.4.6
 */
@Listeners(TestTAPReporter.class)
@Guice(modules = { TapPerlIntegrationModule.class })
public class TestPerlIntegration
{

	private static final Integer SUCCESS_EXIT_CODE = Integer.valueOf(0);

	private static boolean isWindows = System.getProperty("os.name").contains("Windows");
	
	@Inject
	protected TapProducer tapProducer;

	@Test(description = "Tests planned tests in TAP.")
	public void testPlannedTests()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(2);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.OK, 1);
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);

		Assert.assertEquals(testSet.getTestResults().size(), 2);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(2));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] { "--planned=2" }, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	@Test(description = "Tests passed tests in TAP.")
	public void testPassedTests()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(2);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.OK, 1);
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);

		Assert.assertEquals(testSet.getTestResults().size(), 2);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(2));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] { "--passed", "2" }, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	@Test(description = "Tests failed tests in TAP.")
	public void testFailedTests()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(2);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.NOT_OK, 1);
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);

		Assert.assertEquals(testSet.getTestResults().size(), 2);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(2));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] { "--failed", "1" }, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	@Test(description = "Tests TODO'ed tests in TAP.")
	public void testTodoedTests()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(2);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.NOT_OK, 1);
		result1.setDirective(new Directive(DirectiveValues.TODO, "Sample test"));
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);

		Assert.assertEquals(testSet.getTestResults().size(), 2);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(2));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] { "--todo", "1" }, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	@Test(description = "Tests skipped tests in TAP.")
	public void testSkippedTestes()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(2);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.NOT_OK, 1);
		result1.setDirective(new Directive(DirectiveValues.SKIP, "Sample test"));
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);

		Assert.assertEquals(testSet.getTestResults().size(), 2);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(2));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] { "--skipped", "1" }, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	@Test(description = "Tests planned, passed, failed, TODO'ed and skipped tests in TAP.")
	public void testPannedPassedFailedTodoedSkipped()
	{
		TestSet testSet = new TestSet();

		final Plan tapPlan = new Plan(6);
		testSet.setPlan(tapPlan);

		TestResult result1 = new TestResult(StatusValues.NOT_OK, 1);
		result1.setDirective(new Directive(DirectiveValues.TODO, "Sample test"));
		testSet.addTestResult(result1);

		TestResult result2 = new TestResult(StatusValues.OK, 2);
		testSet.addTestResult(result2);
		
		TestResult result3 = new TestResult(StatusValues.OK, 3);
		testSet.addTestResult(result3);
		
		TestResult result4 = new TestResult(StatusValues.NOT_OK, 4);
		result4.setDirective(new Directive(DirectiveValues.SKIP, "Skip" ));
		testSet.addTestResult(result4);
		
		TestResult result5 = new TestResult(StatusValues.OK, 5);
		result5.setDirective(new Directive(DirectiveValues.TODO, "someday who knows" ));
		testSet.addTestResult(result5);
		
		TestResult result6 = new TestResult(StatusValues.NOT_OK, 6);
		testSet.addTestResult(result6);

		Assert.assertEquals(testSet.getTestResults().size(), 6);
		Assert.assertEquals(tapPlan.getLastTestNumber(), Integer.valueOf(6));

		File tempFile = null;
		try
		{
			tempFile = File.createTempFile("tap4j", ".tap");
		} catch (IOException ioe)
		{
			Assert.fail("Failed to create temp file: " + ioe.getMessage(), ioe);
		}

		try
		{
			tapProducer.dump(testSet, tempFile);
		} catch (TapProducerException tpe)
		{
			Assert.fail("Failed to dump Test Set to file [" + tempFile + "]: "
					+ tpe.getMessage(), tpe);
		}

		Integer exitCode = 1;

		try
		{
			exitCode = this.executePerlCommand(
					new String[] {
							"--planned", "6", 
							"--passed", "4", // TODO in not_ok => ok 
							"--failed", "2",
							"--todo", "2", 
							"--skipped", "1"
					}, tempFile);
		} catch (Throwable t)
		{
			Assert.fail("Failed to execute Perl command: " + t.getMessage());
		}

		Assert.assertEquals(exitCode, SUCCESS_EXIT_CODE);
		
		this.deleteTempFile ( tempFile );
	}

	private Integer executePerlCommand( String[] args, File tapFile )
	{
		final File metatap = this.getMetatap();

		Integer errorLevel = Integer.valueOf(1);

		try
		{
			List<String> commands = new LinkedList<String>();
			
			if ( TestPerlIntegration.isWindows )
			{
				commands.add( "cmd" );
				commands.add( "/c" );
			}
			else
			{
				commands.add("/bin/bash");
				commands.add("-c");
			}
			
			StringBuilder perlcommand = new StringBuilder();
			perlcommand.append("perl ");
			perlcommand.append(metatap.getCanonicalPath().toString());
			for ( int i = 0 ; args != null && i < args.length ; ++i )
			{
				perlcommand.append(" " + args[i]);
			}
			
			perlcommand.append(" < ");
			
			perlcommand.append(tapFile.getCanonicalPath().toString());
			
			if ( TestPerlIntegration.isWindows )
			{
				perlcommand.append(" && exit %%ERRORLEVEL%%");
			}
			
			commands.add( perlcommand.toString() );
			
			Process p = Runtime.getRuntime().exec(commands.toArray(new String[0]));

			errorLevel = p.waitFor();
		} 
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}

		return errorLevel;
	}

	/**
	 * Retrieves Perl metatap script.
	 * 
	 * @return Perl metatap script.
	 * @throws RuntimeException
	 */
	private File getMetatap()
	{
		URL url = ClassLoader.getSystemClassLoader().getResource(".");
		File classLoaderRoot = new File(url.getFile());
		File metatap = new File(classLoaderRoot,
				"/../../src/test/perl/metatap");
		if (!metatap.exists())
		{
			throw new RuntimeException("Missing metatap Perl file: "
					+ metatap.toString());
		}
		return metatap;
	}
	
	/**
	 * Tries to delete a temporary file.
	 * 
	 * @param tempFile temporary file.
	 */
	private void deleteTempFile( File tempFile )
	{
		if ( tempFile != null && tempFile.exists() )
		{
			if ( ! tempFile.delete() )
			{
				tempFile.deleteOnExit();
			}
		}
	}

}
