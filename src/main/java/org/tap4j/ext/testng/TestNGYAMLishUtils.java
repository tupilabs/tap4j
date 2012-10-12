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
package org.tap4j.ext.testng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.JavaBeanLoader;
import org.yaml.snakeyaml.reader.UnicodeReader;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
@SuppressWarnings("unchecked") // TODO: explain why this can be unchecked
public final class TestNGYAMLishUtils {
    //TODO: add javadoc
    /**
     * 
     */
    private static final int EXTRA_SPACE = 3;

    /**
     * Date Format used to format a datetime in ISO-8061 for YAMLish diagnostic.
     */
    public static final SimpleDateFormat ISO_8061_DATE_FORMAT = new SimpleDateFormat(
                                                                                     "yyyy-MM-dd'T'HH:mm:ss");

    public static final String LINE_SEPARATOR = LineBreak.UNIX.getString();

    /**
     * Default hidden constructor.
     */
    private TestNGYAMLishUtils() {
        super();
    }

    /**
     * @param testNgTestResult
     * @return Message value
     */
    public static String getMessage(ITestResult testNgTestResult) {
        return "TestNG Test " + testNgTestResult.getName();
    }

    /**
     * @param testNgTestResult
     * @return Severity value
     */
    public static String getSeverity(ITestResult testNgTestResult) {
        String severity = "~";
        if (testNgTestResult.getThrowable() != null) {
            severity = "High";
        }
        return severity;
    }

    /**
     * @param testNgTestResult
     * @return Source value
     */
    public static String getSource(ITestResult testNgTestResult) {
        String source = testNgTestResult.getTestClass().getName() + "#" +
                        testNgTestResult.getMethod().getMethodName();
        return source;
    }

    /**
     * Retrieves the date of the TestNG Test Result start time.
     * 
     * @return Datetime value
     */
    public static String getDatetime(ITestResult testNgTestResult) {
        long ms = testNgTestResult.getStartMillis();
        Date date = new Date(ms);
        return ISO_8061_DATE_FORMAT.format(date);
    }

    /**
     * @param testNgTestResult
     * @return File value
     */
    public static String getFile(ITestResult testNgTestResult) {
        return testNgTestResult.getTestClass().getName();
    }

    /**
     * @param testNgTestResult
     * @return Line value
     */
    public static String getLine(ITestResult testNgTestResult) {
        String line = "~";
        Throwable testNGException = testNgTestResult.getThrowable();
        if (testNGException != null) {
            StringBuilder lookFor = new StringBuilder();
            lookFor.append(testNgTestResult.getTestClass().getName());
            lookFor.append('.');
            lookFor.append(testNgTestResult.getMethod().getMethodName());
            lookFor.append('(');
            lookFor.append(testNgTestResult.getTestClass().getClass()
                .getSimpleName());
            lookFor.append(".java:");

            StackTraceElement[] els = testNGException.getStackTrace();

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
     * @param testNgTestResult
     * @return Name value
     */
    public static String getName(ITestResult testNgTestResult) {
        return testNgTestResult.getName();
    }

    /**
     * @param testNgTestResult
     * @return Extensions value
     */
    public static Object getExtensions(ITestResult testNgTestResult) {
        Object extensions = null;
        Set<String> attributeNames = testNgTestResult.getAttributeNames();
        Iterator<String> iterator = attributeNames.iterator();
        if (iterator.hasNext()) {
            extensions = new LinkedHashMap<String, Object>();
            for (; iterator.hasNext();) {
                String attributeName = iterator.next();
                Object attributeValue = testNgTestResult
                    .getAttribute(attributeName);
                ((Map<String, Object>) extensions).put(attributeName,
                                                       attributeValue);
            }
        } else {
            extensions = '~';
        }
        return extensions;
    }

    /**
     * @param testNgTestResult
     * @return Expected value
     */
    public static String getExpected(ITestResult testNgTestResult) {
        Throwable throwable = testNgTestResult.getThrowable();

        String expected = null;

        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            String stringException = sw.toString();

            String expectedToken = "expected:";
            String butWasToken = " but was:";
            int index = stringException.indexOf(expectedToken);

            if (index > 0) {
                expected = stringException
                    .substring(index + expectedToken.length(),
                               stringException.lastIndexOf(butWasToken));
                index = stringException.indexOf(butWasToken);
            }
        }
        return expected;
    }

    /**
     * @param testNgTestResult
     * @return Actual value
     */
    public static String getActual(ITestResult testNgTestResult) {
        Throwable throwable = testNgTestResult.getThrowable();

        String actual = "~";

        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            String stringException = sw.toString();

            String expectedToken = "expected:";
            String butWasToken = " but was:";
            int index = stringException.indexOf(expectedToken);
            if (index > 0) {
                index = stringException.indexOf(butWasToken);
                int eolIndex = stringException.indexOf(System
                    .getProperty("line.separator"), index);
                actual = stringException
                    .substring(index + butWasToken.length(), eolIndex);
            }
        }

        return actual;
    }

    /**
     * Returns YAMLish multi-line display entry.
     * 
     * @param testNgTestResult TestNG TestResult
     * @return YAMLish multi-line
     */
    public static String getDisplay(ITestResult testNgTestResult) {

        StringBuilder displayBuffer = new StringBuilder();

        String expected = getExpected(testNgTestResult);
        String actual = getActual(testNgTestResult);

        if (StringUtils.isNotEmpty(expected) && StringUtils.isNotEmpty(actual)) {
            int expectedLength = expected.length();
            int actualLength = actual.length();

            int greater = expectedLength;
            if (actualLength > expectedLength) {
                greater = actualLength;
            } else if ("Expected".length() > greater) {
                greater = "Expected".length();
            }

            // Actual length plus the two spaces and an extra for next character
            int greaterPlus3 = greater + EXTRA_SPACE;

            displayBuffer.append("+" + fill(greaterPlus3, '-') + "+" +
                                 fill(greaterPlus3, '-') + "+");
            displayBuffer.append(LINE_SEPARATOR);

            displayBuffer.append("+" + fill(greater, "Got") + "|" +
                                 fill(greater, "Expected") + "+");
            displayBuffer.append(LINE_SEPARATOR);

            displayBuffer.append("+" + fill(greaterPlus3, '-') + "+" +
                                 fill(greaterPlus3, '-') + "+");
            displayBuffer.append(LINE_SEPARATOR);

            displayBuffer.append("+" + fill(greater, actual) + "|" +
                                 fill(greater, expected) + "+");
            displayBuffer.append(LINE_SEPARATOR);

            displayBuffer.append("+" + fill(greaterPlus3, '-') + "+" +
                                 fill(greaterPlus3, '-') + "+");

        } else {
            displayBuffer.append('~');
        }

        return displayBuffer.toString();

    }

    /**
     * @param greater
     * @return
     */
    private static String fill(int greater, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < greater; ++i) {
            sb.append(Character.toString(c));
        }
        return sb.toString();
    }

    /**
     * @param greater
     * @return
     */
    private static String fill(int greater, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(' ');
        sb.append(s);
        int newgreater = greater - s.length();
        sb.append(fill(newgreater + 1, ' '));
        sb.append(' ');
        return sb.toString();
    }

    /**
     * @param testNgTestResult
     * @return Dump value
     */
    public static Object getDump(ITestResult testNgTestResult) {
        Object returnObject = null;
        // TBD: print names
        Object[] parameters = testNgTestResult.getParameters();
        if (parameters.length > 0) {
            returnObject = new LinkedHashMap<String, Object>();
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                ((Map<String, Object>) returnObject).put("param" + (i + 1),
                                                         parameter);
            }
        } else {
            returnObject = "~";
        }
        return returnObject.toString();
    }

    /**
     * @param testNgTestResult
     * @return Error value
     */
    public static String getError(ITestResult testNgTestResult) {
        String error = "~";

        Throwable t = testNgTestResult.getThrowable();

        if (t != null) {
            error = t.getMessage();
        }

        return error;
    }

    /**
     * @param testNgTestResult
     * @return Backtrace value
     */
    public static String getBacktrace(ITestResult testNgTestResult) {
        StringBuilder stackTrace = new StringBuilder();

        Throwable throwable = testNgTestResult.getThrowable();

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

    /*
     * Methods from SnakeYAML project. See
     * http://code.google.com/p/snakeyaml/wiki/TestingNG_YAML for more.
     */

    public static StringBuilder toYaml(XmlSuite suite) {
        StringBuilder result = new StringBuilder();

        maybeAdd(result, "name", suite.getName(), null);
        maybeAdd(result, "junit", suite.isJUnit(), XmlSuite.DEFAULT_JUNIT);
        maybeAdd(result, "verbose", suite.getVerbose(),
                 XmlSuite.DEFAULT_VERBOSE);
        maybeAdd(result, "threadCount", suite.getThreadCount(),
                 XmlSuite.DEFAULT_THREAD_COUNT);
        maybeAdd(result, "dataProviderThreadCount",
                 suite.getDataProviderThreadCount(),
                 XmlSuite.DEFAULT_DATA_PROVIDER_THREAD_COUNT);
        maybeAdd(result, "timeOut", suite.getTimeOut(), null);
        maybeAdd(result, "parallel", suite.getParallel(),
                 XmlSuite.DEFAULT_PARALLEL);
        maybeAdd(result, "skipFailedInvocationCounts",
                 suite.skipFailedInvocationCounts(),
                 XmlSuite.DEFAULT_SKIP_FAILED_INVOCATION_COUNTS);

        toYaml(result, "parameters", "", suite.getParameters());
        toYaml(result, suite.getPackages());

        if (suite.getListeners().size() > 0) {
            result.append("listeners:\n");
            toYaml(result, "  ", suite.getListeners());
        }

        if (suite.getPackages().size() > 0) {
            result.append("packages:\n");
            toYaml(result, suite.getPackages());
        }
        toYaml(result, "listeners", suite.getListeners());
        if (suite.getTests().size() > 0) {
            result.append("tests:\n");
            for (XmlTest t : suite.getTests()) {
                toYaml(result, "  ", t);
            }
        }

        if (suite.getChildSuites().size() > 0) {
            result.append("suite-files:\n");
            toYaml(result, "  ", suite.getSuiteFiles());
        }

        return result;
    }

    /**
     * @param sb
     * @param key
     * @param value
     * @param def
     */
    private static void maybeAdd(StringBuilder sb, String key, Object value,
                                 Object def) {
        maybeAdd(sb, "", key, value, def);
    }

    /**
     * @param sb
     * @param sp
     * @param key
     * @param value
     * @param def
     */
    private static void maybeAdd(StringBuilder sb, String sp, String key,
                                 Object value, Object def) {
        if (value != null && !value.equals(def)) {
            sb.append(sp).append(key).append(": ").append(value.toString())
                .append("\n");
        }
    }

    /**
     * @param result
     * @param sp
     * @param t
     */
    private static void toYaml(StringBuilder result, String sp, XmlTest t) {
        String sp2 = sp + "  ";
        result.append(sp).append("- name: ").append(t.getName()).append("\n");

        maybeAdd(result, sp2, "junit", t.isJUnit(), XmlSuite.DEFAULT_JUNIT);
        maybeAdd(result, sp2, "verbose", t.getVerbose(),
                 XmlSuite.DEFAULT_VERBOSE);
        maybeAdd(result, sp2, "timeOut", t.getTimeOut(), null);
        maybeAdd(result, sp2, "parallel", t.getParallel(),
                 XmlSuite.DEFAULT_PARALLEL);
        maybeAdd(result, sp2, "skipFailedInvocationCounts",
                 t.skipFailedInvocationCounts(),
                 XmlSuite.DEFAULT_SKIP_FAILED_INVOCATION_COUNTS);

        maybeAdd(result, "preserveOrder", sp2, t.getPreserveOrder(), "false"); // TBD:
                                                                               // is
                                                                               // it
                                                                               // the
                                                                               // default
                                                                               // value?

        toYaml(result, "parameters", sp2, t.getParameters());

        if (t.getIncludedGroups().size() > 0) {
            result.append(sp2).append("includedGroups: [ ")
                .append(StringUtils.join(t.getIncludedGroups(), ","))
                .append(" ]\n");
        }

        if (t.getExcludedGroups().size() > 0) {
            result.append(sp2).append("excludedGroups: [ ")
                .append(StringUtils.join(t.getExcludedGroups(), ","))
                .append(" ]\n");
        }

        Map<String, List<String>> mg = t.getMetaGroups();
        if (mg.size() > 0) {
            result.append(sp2).append("metaGroups: { ");
            boolean first = true;
            for (String group : mg.keySet()) {
                if (!first) {
                    result.append(", ");
                }
                result.append(group).append(": [ ")
                    .append(StringUtils.join(mg.get(group), ",")).append(" ] ");
                first = false;
            }
            result.append(" }\n");
        }

        if (t.getXmlPackages().size() > 0) {
            result.append(sp2).append("xmlPackages:\n");
            for (XmlPackage xp : t.getXmlPackages()) {
                toYaml(result, sp2 + "  - ", xp);
            }
        }

        if (t.getXmlClasses().size() > 0) {
            result.append(sp2).append("classes:\n");
            for (XmlClass xc : t.getXmlClasses()) {
                toYaml(result, sp2 + "  ", xc);
            }
        }

        result.append("\n");
    }

    /**
     * @param result
     * @param sp2
     * @param xc
     */
    private static void toYaml(StringBuilder result, String sp2, XmlClass xc) {
        List<XmlInclude> im = xc.getIncludedMethods();
        List<String> em = xc.getExcludedMethods();
        String name = im.size() > 0 || em.size() > 0 ? "name: " : "";

        result.append(sp2).append("- " + name).append(xc.getName())
            .append("\n");
        if (im.size() > 0) {
            result.append(sp2 + "  includedMethods:\n");
            for (XmlInclude xi : im) {
                toYaml(result, sp2 + "    ", xi);
            }
        }

        if (em.size() > 0) {
            result.append(sp2 + "  excludedMethods:\n");
            toYaml(result, sp2 + "    ", em);
        }
    }

    /**
     * @param result
     * @param sp2
     * @param xi
     */
    private static void toYaml(StringBuilder result, String sp2, XmlInclude xi) {
        result.append(sp2 + "- " + xi.getName()).append("\n");
    }

    /**
     * @param result
     * @param sp
     * @param strings
     */
    private static void toYaml(StringBuilder result, String sp,
                               List<String> strings) {
        for (String l : strings) {
            result.append(sp).append("- ").append(l).append("\n");
        }
    }

    // private static final String SP = "  ";

    /**
	 * 
	 */
    private static void toYaml(StringBuilder sb, List<XmlPackage> packages) {
        if (packages.size() > 0) {
            sb.append("packages:\n");
            for (XmlPackage p : packages) {
                toYaml(sb, "  ", p);
            }
        }
        for (XmlPackage p : packages) {
            toYaml(sb, "  ", p);
        }
    }

    /**
     * @param sb
     * @param sp
     * @param p
     */
    private static void toYaml(StringBuilder sb, String sp, XmlPackage p) {
        sb.append(sp).append("name: ").append(p.getName()).append("\n");

        generateIncludeExclude(sb, sp, "includes", p.getInclude());
        generateIncludeExclude(sb, sp, "excludes", p.getExclude());
    }

    /**
     * @param sb
     * @param sp
     * @param key
     * @param includes
     */
    private static void generateIncludeExclude(StringBuilder sb, String sp,
                                               String key, List<String> includes) {
        if (includes.size() > 0) {
            sb.append(sp).append("  ").append(key).append("\n");
            for (String inc : includes) {
                sb.append(sp).append("    ").append(inc);
            }
        }
    }

    /**
     * @param map
     * @param out
     */
    private static void mapToYaml(Map<String, String> map, StringBuilder out) {
        if (map.size() > 0) {
            out.append("{ ");
            boolean first = true;
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (!first) {
                    out.append(", ");
                }
                first = false;
                out.append(e.getKey() + ": " + e.getValue());
            }
            out.append(" }\n");
        }
    }

    /**
     * @param sb
     * @param key
     * @param sp
     * @param parameters
     */
    private static void toYaml(StringBuilder sb, String key, String sp,
                               Map<String, String> parameters) {
        if (parameters.size() > 0) {
            sb.append(sp).append(key).append(": ");
            mapToYaml(parameters, sb);
        }
    }

    /**
     * @param suite
     * @param name
     * @param target
     */
    @SuppressWarnings({
        "unused", "rawtypes"
    })
    private static void addToMap(Map suite, String name, Map target) {
        List<Map<String, String>> parameters = (List<Map<String, String>>) suite
            .get(name);
        if (parameters != null) {
            for (Map<String, String> parameter : parameters) {
                for (Map.Entry p : parameter.entrySet()) {
                    target.put(p.getKey(), p.getValue().toString());
                }
            }
        }
    }

    /**
     * @param suite
     * @param name
     * @param target
     */
    @SuppressWarnings({
        "unused", "rawtypes"
    })
    private static void addToList(Map suite, String name, List target) {
        List<Map<String, String>> parameters = (List<Map<String, String>>) suite
            .get(name);
        if (parameters != null) {
            for (Map<String, String> parameter : parameters) {
                for (Map.Entry p : parameter.entrySet()) {
                    target.add(p.getValue().toString());
                }
            }
        }
    }

    /**
     * @param filePath
     * @param is
     * @return XMLSuite
     * @throws FileNotFoundException
     */
    public static XmlSuite parse(String filePath, InputStream is)
        throws FileNotFoundException {
        JavaBeanLoader<XmlSuite> loader = new JavaBeanLoader<XmlSuite>(
                                                                       XmlSuite.class);
        if (is == null) {
            is = new FileInputStream(new File(filePath));
        }
        XmlSuite result = loader.load(new UnicodeReader(is)); // UnicodeReader
                                                              // used to
                                                              // respect BOM
        result.setFileName(filePath);
        // DEBUG
        // System.out.println("[Yaml] " + result.toXml());

        // Adjust XmlTest parents
        for (XmlTest t : result.getTests()) {
            t.setSuite(result);
        }

        return result;
    }

    // private static void setField( Object xml, Map<?, ?> map, String key,
    // String methodName, Class<?> parameter )
    // {
    // Object o = map.get(key);
    // if (o != null)
    // {
    // Method m;
    // try
    // {
    // m = xml.getClass().getMethod(methodName, parameter);
    // m.invoke(xml, o);
    // } catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }
    // }

}
