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

package org.tap4j.ext.testng.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tap4j.ext.testng.model.TestNGAttribute;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;
import org.testng.internal.ResultMap;

/**
 * Utility class with methods to support TAP generation with TestNG.
 * 
 * @since 1.1
 */
public final class TapTestNGUtil {

    /**
     * Hidden constructor.
     */
    private TapTestNGUtil() {
        super();
    }

    /**
     * A constant that defines a comparator for TestNG TestResults. This
     * comparator returns the TestNG TestResults ordered by execution date. For
     * example, if the test A ran at 08:00 and the test B ran at 07:00, it will
     * say that the correct order for these elements is [B, A].
     */
    public static final Comparator<ITestResult> EXECUTION_DATE_COMPARATOR = new ExecutionDateCompator();

    /**
     * Generates a TAP TestResult from a given TestNG TestResult.
     * 
     * @param testResult TestNG Test Result
     * @param number TAP Test Number
     * @param yaml whether YAML is enabled or not
     * @return TAP TestResult
     */
    public static TestResult generateTAPTestResult(ITestResult testResult,
                                                   Integer number, boolean yaml) {
        final TestResult tapTestResult = new TestResult();

        String testResultDescription = generateTAPTestResultDescription(testResult);
        tapTestResult.setDescription(testResultDescription);

        TapTestNGUtil.setTapTestResultStatus(tapTestResult,
                                              testResult.getStatus());

        if (yaml) {
            TapTestNGYamlUtil.createTestNGYAMLishData(tapTestResult, testResult);
        }

        return tapTestResult;
    }

    /**
     * Generates a TAP TestResult description with full qualified class name
     * concatenated with the character ':' and the test method.
     * 
     * @param testResult TestNG TestResult
     * @return Name of TAP Test Result
     */
    public static String generateTAPTestResultDescription(ITestResult testResult) {
        StringBuilder description = new StringBuilder();
        description.append("- "); // An extra space is added before the
                                  // description by the TAP Representer
        description.append(testResult.getTestClass().getName());
        description.append(':');
        description.append(testResult.getMethod().getMethodName());
        return description.toString();
    }

    /**
     * Sets the StatusValue into a TAP TestResult. In cases where the
     * StatusValue is equal SKIP a Directive is also created and added to the
     * TAP Test Result.
     * 
     * @param tapTestResult TAP TestResult
     * @param status TestNG Test Status (Success, Skip, or any other that is
     *        treated as Failed in TAP)
     */
    public static void setTapTestResultStatus(TestResult tapTestResult,
                                              int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                tapTestResult.setStatus(StatusValues.OK);
                break;
            case ITestResult.SKIP:
                tapTestResult.setStatus(StatusValues.NOT_OK);
                Directive skip = new Directive(DirectiveValues.SKIP,
                                               "TestNG test was skipped");
                tapTestResult.setDirective(skip);
                break;
            default:
                tapTestResult.setStatus(StatusValues.NOT_OK);
                break;
        }
    }

    /**
     * Return an ordered list of TestNG TestResult from a given TestNG Test
     * Context.
     * 
     * @param testContext TestNG Test Context
     * @return Ordered list of TestNG TestResults
     */
    public static List<ITestResult> getTestNGResultsOrderedByExecutionDate(ITestContext testContext) {
        Map<String, IResultMap> results = new LinkedHashMap<String, IResultMap>();

        results.put("passed", testContext.getPassedTests());
        results.put("failed", testContext.getFailedTests());
        results.put("failedBut",
                    testContext.getFailedButWithinSuccessPercentageTests());
        results.put("skipped", testContext.getSkippedTests());

        ResultMap total = new ResultMap();

        addAll(total, results.get("passed"));
        addAll(total, results.get("failed"));
        addAll(total, results.get("failedBut"));
        addAll(total, results.get("skipped"));

        ITestNGMethod[] allMethodsInCtx = testContext.getAllTestMethods();
        for (int i = 0; i < allMethodsInCtx.length; i++) {
            ITestNGMethod methodInCtx = allMethodsInCtx[i];

            Collection<ITestNGMethod> allMethodsFound = total.getAllMethods();
            boolean exists = false;
            for (ITestNGMethod methodFound : allMethodsFound) {
                if (methodInCtx.getTestClass().getName()
                        .equals(methodFound.getTestClass().getName())
                        && methodInCtx
                                .getConstructorOrMethod()
                                .getName()
                                .equals(methodFound.getConstructorOrMethod()
                                        .getName())) {
                    exists = true;
                }
            }
            if (!exists) {
                ITestResult skippedTestResult = 
                        new org.testng.internal.TestResult(methodInCtx.getTestClass(),
                             methodInCtx.getInstance(), methodInCtx, null, 
                             testContext.getStartDate().getTime(), testContext.getEndDate().getTime(), 
                             testContext);
                skippedTestResult.setStatus(ITestResult.SKIP);
                total.addResult(skippedTestResult, methodInCtx);
            }
        }

        List<ITestResult> testNGTestResults = new ArrayList<ITestResult>(
                                                                         total
                                                                             .getAllResults());
        Collections.sort(testNGTestResults, EXECUTION_DATE_COMPARATOR);

        return testNGTestResults;
    }

    /**
     * Return an ordered list of TestNG TestResult from a given TestNG Test
     * Context.
     * 
     * @param total TestNG Result Map
     * @return Ordered list of TestNG TestResults
     */
    public static List<ITestResult> getTestNGResultsOrderedByExecutionDate(ResultMap total) {
        List<ITestResult> testNGTestResults = new ArrayList<ITestResult>(
                                                                         total
                                                                             .getAllResults());
        Collections.sort(testNGTestResults, EXECUTION_DATE_COMPARATOR);

        return testNGTestResults;
    }

    /**
     * Adds all ITestResult's inside the map object inside the total one.
     * 
     * @param total ResultMap that holds the total of IResultMap's.
     * @param map An IResultMap object.
     */
    public static void addAll(ResultMap total, IResultMap map) {
        for (ITestResult testResult : map.getAllResults()) {
            total.addResult(testResult, testResult.getMethod());
        }
    }

    /**
     * Fills the TestNG Attributes from the context into the TestNG Test Result.
     * 
     * @param tr
     * @param ctx
     */
    public static void fillAttributes(ITestResult tr, ITestContext ctx) {
        final Set<String> attrsNames = ctx.getAttributeNames();
        for (String attr : attrsNames) {
            Object o = ctx.getAttribute(attr);
            if (o instanceof TestNGAttribute) {
                TestNGAttribute tapAttr = (TestNGAttribute) o;
                ITestNGMethod testNGMethod = tr.getMethod();
                ConstructorOrMethod constructorOrMethod = testNGMethod.getConstructorOrMethod();
                if (constructorOrMethod.getMethod() == tapAttr.getMethod()) {
                    tr.setAttribute(attr, tapAttr.getValue());
                }
            }
        }
    }

}

class ExecutionDateCompator implements Comparator<ITestResult>, Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(ITestResult o1, ITestResult o2) {
        if (o1.getStartMillis() > o2.getStartMillis()) {
            return 1;
        }
        return -1;
    }
}
