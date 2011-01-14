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
package br.eti.kinoshita.tap4j.ext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestResult;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public final class YAMLishUtils 
{
	
	/**
	 * Date Format used to format a datetime in ISO-8061 for YAMLish diagnostic.
	 */
	public static final SimpleDateFormat ISO_8061_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Default hidden constructor.
	 */
	private YAMLishUtils()
	{
		super();
	}
	
	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getMessage(ITestResult testNgTestResult)
	{
		return "TestNG Test " + testNgTestResult.getName();
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getSeverity(ITestResult testNgTestResult) 
	{
		String severity = "~";
		if ( testNgTestResult.getThrowable() != null )
		{
			severity = "High";
		}
		return severity;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getSource(ITestResult testNgTestResult) 
	{
		String source = testNgTestResult.getInstance().getClass().getName() + 
		"#" +
		testNgTestResult.getMethod().getMethodName();
		return source;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getDatetime(ITestResult testNgTestResult) 
	{
		long currentTimeMillis = System.currentTimeMillis();
		Date date = new Date( currentTimeMillis );
		String iso8061Datetime = ISO_8061_DATE_FORMAT.format( date );
		return iso8061Datetime;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getFile(ITestResult testNgTestResult) 
	{
		String file = testNgTestResult.getInstance().getClass().getName();
		return file;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getLine(ITestResult testNgTestResult) 
	{
		String line = "~";
		Throwable testNGException = testNgTestResult.getThrowable();
		if ( testNGException != null )
		{
			StackTraceElement[] els = testNGException.getStackTrace();
			for (int i = 0; i < els.length; i++) 
			{
				StackTraceElement el = els[i];
				StringBuilder lookFor = new StringBuilder();
				lookFor.append("at ");
				lookFor.append( testNgTestResult.getInstance().getClass().getName() );
				lookFor.append('.');
				lookFor.append( testNgTestResult.getMethod().getMethodName() );
				lookFor.append( '(' );
				lookFor.append( testNgTestResult.getInstance().getClass().getSimpleName() );
				lookFor.append( ".java:" );
				int index = el.toString().indexOf(lookFor.toString());
				if ( index  > 0 )
				{
					line = el.toString().substring(index, el.toString().lastIndexOf(')'));
					break;
				}
			}
		}
		return line;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getName(ITestResult testNgTestResult) 
	{
		String name = testNgTestResult.getName();
		return name;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static Object getExtensions(ITestResult testNgTestResult) 
	{
		// TBD: implement it
		// TBD: read http://java.dzone.com/articles/yaml-forgotten-victim-format?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+javalobby%2Ffrontpage+%28Javalobby+%2F+Java+Zone%29
		return null;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getExpected(ITestResult testNgTestResult) 
	{
		Throwable throwable = testNgTestResult.getThrowable();
		
		String expected = null;
		
		if ( throwable != null )
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace( pw );
			
			String stringException = sw.toString();
			
			String expectedToken = "expected:";
			String butWasToken = " but was:";
			int index = stringException.toString().indexOf(expectedToken.toString());
			if ( index  > 0 )
			{
				expected = stringException.toString().substring(index+expectedToken.length(), stringException.toString().lastIndexOf(butWasToken));
				index = stringException.toString().indexOf(butWasToken);
			}
		}
		return expected;
	}
	
	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getActual(ITestResult testNgTestResult) 
	{
		Throwable throwable = testNgTestResult.getThrowable();
		
		String actual = "~";
		
		if ( throwable != null )
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace( pw );
			
			String stringException = sw.toString();
			
			String expectedToken = "expected:";
			String butWasToken = " but was:";
			int index = stringException.toString().indexOf(expectedToken.toString());
			if ( index  > 0 )
			{
				index = stringException.toString().indexOf(butWasToken);
				int eolIndex = stringException.toString().indexOf(System.getProperty("line.separator"), index);
				actual = stringException.toString().substring(index+butWasToken.length(), eolIndex);
			}
		}
		
		return actual;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getDisplay(ITestResult testNgTestResult) 
	{
		
		StringBuilder display = new StringBuilder();;
		
		String expected = getExpected(testNgTestResult);
		String actual = getActual(testNgTestResult);
		
		if ( StringUtils.isNotEmpty(expected) && StringUtils.isNotEmpty(actual) )
		{
			int expectedLength = expected.length();
			int actualLength = actual.length();
			
			int greater = expectedLength;
			if ( actualLength > expectedLength )
			{
				greater = actualLength;
			}
			else if ( "Expected".length() > greater )
			{
				greater = "Expected".length();
			}
			
			// Actual length plus the two spaces and an extra for next character
			int greaterPlus3 = greater+3;
			
			display.append('>');
			display.append(System.getProperty("line.separator"));
			display.append("  +" + fill(greaterPlus3, '-') + "+" + fill(greaterPlus3, '-') + "+");
			display.append(System.getProperty("line.separator"));
			display.append("  |" + fill(greater, "Got") + "|" + fill(greater, "Expected") + "+");
			display.append(System.getProperty("line.separator"));
			display.append("  +" + fill(greaterPlus3, '-') + "+" + fill(greaterPlus3, '-') + "+");
			display.append(System.getProperty("line.separator"));
			display.append("  |" + fill(greater, actual) + "|" + fill(greater, expected) + "+");
			display.append(System.getProperty("line.separator"));
			display.append("  +" + fill(greaterPlus3, '-') + "+" + fill(greaterPlus3, '-') + "+");
			display.append(System.getProperty("line.separator"));
			
		}
		else
		{
			display.append('~');
		}
		
		return display.toString();
		
	}

	/**
	 * @param greater
	 * @return
	 */
	private static String fill(int greater, char c ) 
	{
		StringBuilder sb = new StringBuilder();
		for ( int i = 0 ; i < greater ; ++i )
		{
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * @param greater
	 * @return
	 */
	private static String fill(int greater, String s ) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(' ');
		sb.append( s );
		int newgreater = greater - s.length();
		sb.append( fill( newgreater+1, ' ') );
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object getDump(ITestResult testNgTestResult) 
	{
		Object returnObject = null;
		// TBD: print names
		Object[] parameters = testNgTestResult.getParameters();
		if( parameters.length > 0 )
		{
			returnObject = new LinkedHashMap<String, Object>();
			for (int i = 0; i < parameters.length; i++) 
			{
				Object parameter  = parameters[i];
				((Map<String, Object>)returnObject).put("param"+(i+1), parameter);
			}
		}
		else 
		{
			returnObject = "~";
		}
		return returnObject;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getError(ITestResult testNgTestResult) 
	{
		String error = "~";
		
		Throwable t = testNgTestResult.getThrowable();
		
		if ( t != null  )
		{
			error = t.getMessage();
		}
		
		return error;
	}

	/**
	 * @param testNgTestResult
	 * @return
	 */
	public static String getBacktrace(ITestResult testNgTestResult) 
	{
		StringBuilder stackTrace = new StringBuilder();
		
		Throwable throwable = testNgTestResult.getThrowable();
		
		if ( throwable != null )
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			throwable.printStackTrace( pw );
			String stackTraceString = sw.toString();
			stackTraceString = stackTraceString.replace("\r", "");
			
			StringTokenizer st = new StringTokenizer(stackTraceString, System.getProperty("line.separator"));
			if( st.hasMoreTokens() )
			{
				stackTrace.append(">");
				stackTrace.append(System.getProperty("line.separator"));
			}
			while( st.hasMoreTokens() )
			{
				String stackTraceLine = st.nextToken();
				stackTrace.append("  ");
				stackTrace.append( stackTraceLine );
				stackTrace.append(System.getProperty("line.separator"));
			}
		}
		else
		{
			stackTrace.append("~");
		}
		
		return stackTrace.toString();
	}
	
	
	
}
