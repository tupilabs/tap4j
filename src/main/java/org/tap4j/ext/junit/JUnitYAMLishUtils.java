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
package org.tap4j.ext.junit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.yaml.snakeyaml.DumperOptions.LineBreak;

/**
 * @author Cesar Fernandes de Almeida
 * @since 1.4.3
 */
public final class JUnitYAMLishUtils {

    /**
     * Date Format used to format a datetime in ISO-8061 for YAMLish diagnostic.
     */
    public static final SimpleDateFormat ISO_8061_DATE_FORMAT = new SimpleDateFormat(
                                                                                     "yyyy-MM-dd'T'HH:mm:ss");

    public static final String LINE_SEPARATOR = LineBreak.UNIX.getString();

    /**
     * Default hidden constructor.
     */
    private JUnitYAMLishUtils() {
        super();
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
        return testClass + "#" + testMethod;
    }

    /**
     * Get a date time string
     * 
     * @return date time string
     */
    public static String getDatetime() {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        return ISO_8061_DATE_FORMAT.format(date);
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
     * Get tested method name
     * 
     * @param testMethod
     * @return tested method name
     */
    public static String getName(JUnitTestData testMethod) {
        return extractMethodName(testMethod.getDescription());
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

    /**
     * Extract the class name from a given junit test description
     * 
     * @param description
     * @return a class name
     */
    public static String extractClassName(Description description) {
        String displayName = description.getDisplayName();

        String regex = "^" + "[^\\(\\)]+" // non-parens
                       + "\\((" // then an open-paren (start matching a group)
                       + "[^\\\\(\\\\)]+" // non-parens
                       + ")\\)" + "$";
        // System.out.println(regex);
        final Pattern parens = Pattern.compile(regex); // then a close-paren
                                                       // (end group match)
        Matcher m = parens.matcher(displayName);
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
        String className = extractClassName(description);
        String[] splitClassName = className.split("\\.");

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
        String[] splitDisplayName = description.getDisplayName().split("\\(");

        if (splitDisplayName.length > 0) {
            methodName = splitDisplayName[0];
        }

        return methodName;
    }
}
