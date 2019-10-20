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
package org.tap4j.ext.junit.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.tap4j.ext.junit.model.JUnitTestData;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.yaml.snakeyaml.DumperOptions.LineBreak;

/**
 * JUnit TAP extension utility class.
 *
 * @since 1.4.3
 */
public final class TapJUnitUtil {
    
    public static final String LINE_SEPARATOR = LineBreak.UNIX.getString();

    /**
     * Default constructor.
     */
    private TapJUnitUtil() {
        super();
    }

    /**
     * Generate TAP test result
     * 
     * @param testMethod
     * @param number
     * @return TestResult
     */
    public static TestResult generateTAPTestResult(JUnitTestData testMethod,
            Integer number, boolean yaml) {
        final TestResult tapTestResult = new TestResult();

        final String testResultDescription = generateTAPTestResultDescription(testMethod);
        tapTestResult.setDescription(testResultDescription);

        setTapTestStatus(tapTestResult, testMethod);
        
        if (yaml) {
            String message = getMessage(testMethod);
            String severity = getSeverity(testMethod);
            String source = getSource(
                    extractMethodName(testMethod.getDescription()),
                    extractClassName(testMethod.getDescription()));
            String datetime = getDatetime();
            String file = getFile(testMethod);
            String line = getLine(testMethod);
            String name = getName(testMethod);
            String error = getError(testMethod);
            String backtrace = getBacktrace(testMethod);
            TapJUnitYamlUtil.createJUnitYAMLishData(tapTestResult, message,
                    severity, source, datetime, file, line, name, error, backtrace);
        }
        
        return tapTestResult;
    }

    /**
     * Get the tap test description
     * 
     * @param testMethod
     * @return the tap test description
     */
    public static String generateTAPTestResultDescription(JUnitTestData testMethod) {
        final StringBuilder description = new StringBuilder();

        // An extra space is added before the description by the TAP Representer
        description.append("- ");
        description.append(extractClassName(testMethod
            .getDescription()));
        description.append(':');
        description.append(extractMethodName(testMethod
            .getDescription()));

        return description.toString();
    }

    /**
     * Set the tap status
     * 
     * @param tapTestResult
     * @param testMethod
     */
    public static void setTapTestStatus(TestResult tapTestResult,
                                        JUnitTestData testMethod) {
        if (testMethod.isIgnored()) {
            tapTestResult.setStatus(StatusValues.NOT_OK);
            Directive skip = new Directive(DirectiveValues.SKIP,
                                           "JUnit test was skipped");
            tapTestResult.setDirective(skip);
        } else if (testMethod.isFailed()) {
            tapTestResult.setStatus(StatusValues.NOT_OK);
        } else {
            tapTestResult.setStatus(StatusValues.OK);
        }
    }

    /**
     * Extract the class name from a given junit test description
     * 
     * @param description
     * @return a class name
     */
    public static String extractClassName(Description description) {
        final String displayName = description.getDisplayName();

        final String regex = "^" + "[^\\(\\)]+" // non-parens
                       + "\\((" // then an open-paren (start matching a group)
                       + "[^\\\\(\\\\)]+" // non-parens
                       + ")\\)" + "$";
        // System.out.println(regex);
        final Pattern parens = Pattern.compile(regex); // then a close-paren
                                                       // (end group match)
        final Matcher m = parens.matcher(displayName);
        if (!m.find()) {
            return displayName;
        }
        return m.group(1);
    }

    /**
     * Extract the simple class name from a given junit test description
     * 
     * @param description
     * @return a simple class name
     */
    public static String extractSimpleClassName(Description description) {
        String simpleClassName = null;
        final String className = extractClassName(description);
        final String[] splitClassName = className.split("\\.");

        if (splitClassName.length > 0) {
            simpleClassName = splitClassName[splitClassName.length - 1];
        }

        return simpleClassName;
    }
    
    /**
     * Get the tested method name
     * 
     * @param description
     * @return tested methode name
     */
    public static String extractMethodName(Description description) {
        String methodName = null;
        final String[] splitDisplayName = description.getDisplayName().split("\\(");

        if (splitDisplayName.length > 0) {
            methodName = splitDisplayName[0];
        }

        return methodName;
    }

    /**
     * Get the file name of the tested method
     * 
     * @param testMethod
     * @return the file name
     */
    public static String getFile(JUnitTestData testMethod) {
        return extractClassName(testMethod.getDescription());
    }

    /**
     * Get tested method name
     * 
     * @param testMethod
     * @return tested method name
     */
    public static String getName(JUnitTestData testMethod) {
        return extractMethodName(testMethod.getDescription());
    }

    /**
     * Get the line of the error in the exception info
     * 
     * @param testMethod
     * @return line of the error in the exception info
     */
    public static String getLine(JUnitTestData testMethod) {
        String line = "~";
        Throwable testException = testMethod.getFailException();
        if (testException != null) {
            StringBuilder lookFor = new StringBuilder();
            lookFor.append(extractClassName(testMethod.getDescription()));
            lookFor.append('.');
            lookFor.append(extractMethodName(testMethod.getDescription()));
            lookFor.append('(');
            lookFor.append(extractSimpleClassName(testMethod.getDescription()));
            lookFor.append(".java:");

            StackTraceElement[] els = testException.getStackTrace();

            for (int i = 0; i < els.length; i++) {
                StackTraceElement el = els[i];
                line = getLineNumberFromExceptionTraceLine(el.toString(),
                                                           lookFor.toString());
                if (line.equals("") == Boolean.FALSE) {
                    break;
                }
            }
        }
        return line;
    }

    /**
     * Get the error line number from the exception stack trace
     * 
     * @param exceptionTraceLine
     * @param substrToSearch
     * @return error line number
     */
    public static String getLineNumberFromExceptionTraceLine(String exceptionTraceLine,
                                                             String substrToSearch) {
        String lineNumber = "";
        int index = exceptionTraceLine.indexOf(substrToSearch);
        if (index >= 0) {
            int length = substrToSearch.length() + index;
            if (exceptionTraceLine.lastIndexOf(')') > length) {
                lineNumber = exceptionTraceLine
                    .substring(length, exceptionTraceLine.lastIndexOf(')'));
            }
        }
        return lineNumber;
    }
    
    /**
     * Get the error message from a given failed JUnit test result
     * 
     * @param testMethod
     * @return error message from a given failed JUnit test result
     */
    public static String getError(JUnitTestData testMethod) {
        String error = "~";
        Throwable t = testMethod.getFailException();
        if (t != null) {
            error = t.getMessage();
        }
        return error;
    }

    /**
     * Generate a message with the name of the tested method
     * 
     * @param testMethod
     * @return test message
     */
    public static String getMessage(JUnitTestData testMethod) {
        return "JUnit 4.0 Test " + testMethod.getDescription().getDisplayName();
    }

    /**
     * Get the severity of the test
     * 
     * @param testMethod
     * @return severity
     */
    public static String getSeverity(JUnitTestData testMethod) {
        String severity = "~";
        if (testMethod.getFailException() != null) {
            severity = "High";
        }
        return severity;
    }

    /**
     * @param testMethod
     * @param testClass
     * @return test source
     */
    public static String getSource(String testMethod, String testClass) {
        return testClass + ":" + testMethod;
    }

    /**
     * Get a date time string
     * 
     * @return date time string
     */
    public static String getDatetime() {
        long currentTimeMillis = System.currentTimeMillis();
        final Date date = new Date(currentTimeMillis);
        // ISO-8061 for YAMLish diagnostic
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
    }

    /**
     * Get the backtrace from a given failed JUnit test result
     * 
     * @param testMethod
     * @return Backtrace from a given failed JUnit test result
     */
    public static String getBacktrace(JUnitTestData testMethod) {
        StringBuilder stackTrace = new StringBuilder();

        Throwable throwable = testMethod.getFailException();

        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTraceString = sw.toString();
            stackTraceString = stackTraceString.trim().replaceAll("\\r\\n",
                    "\n");

            StringTokenizer st = new StringTokenizer(stackTraceString,
                    LINE_SEPARATOR);

            while (st.hasMoreTokens()) {
                String stackTraceLine = st.nextToken();
                stackTrace.append(stackTraceLine);
                stackTrace.append(LINE_SEPARATOR);
            }

        } else {
            stackTrace.append('~');
        }

        return stackTrace.toString();
    }
}
