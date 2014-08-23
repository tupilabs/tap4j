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
package org.tap4j.ext.junit.listener;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.tap4j.ext.junit.model.JUnitTestData;
import org.tap4j.ext.junit.util.TapJUnitUtil;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducer;
import org.tap4j.representer.DumperOptions;
import org.tap4j.representer.Representer;
import org.tap4j.representer.Tap13Representer;

/**
 * Abstract listener that gathers all the test methods and provides methods for
 * generating TAP reports per class, method or suite. 
 *
 * @since 1.4.3
 */
abstract class TapListener extends RunListener {
    public enum Type {
        METHOD, CLASS, SUITE, ALL
    };

    /**
     * List of test methods.
     */
    private List<JUnitTestData> testMethodsList = new LinkedList<JUnitTestData>();
    /**
     * TAP Producer.
     */
    private Producer tapProducer = new TapProducer();

    /**
     * Default constructor.
     */
    public TapListener() {
        super();
        if (isYaml()) {
            DumperOptions options = new DumperOptions();
            options.setPrintDiagnostics(true);
            Representer representer = new Tap13Representer(options);
            this.tapProducer = new TapProducer(representer);
        } else {
            this.tapProducer = new TapProducer();
        }
    }

    /**
     * @return <code>true</code> when output YAML is enabled.
     */
    public abstract boolean isYaml();

    /**
     * Called right before any tests from a specific class are run.
     * 
     * @see org.junit.runner.notification.RunListener#testRunStarted(org.junit.runner.Description)
     */
    public void testRunStarted(Description description) throws Exception {
        if (isYaml()) {
            DumperOptions options = new DumperOptions();
            options.setPrintDiagnostics(true);
            Representer representer = new Tap13Representer(options);
            this.tapProducer = new TapProducer(representer);
        } else {
            this.tapProducer = new TapProducer();
        }
        this.testMethodsList = new LinkedList<JUnitTestData>();
    }

    /**
     * Called right after all tests from a specific class are run.
     * 
     * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
     */
    public abstract void testRunFinished(Result result) throws Exception;

    /**
     * Called when a specific test has started.
     * 
     * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
     */
    public void testStarted(Description description) throws Exception {
        this.setTestInfo(description);
    }
    
    /**
     * Set test info
     * 
     * @param description
     */
    protected void setTestInfo(Description description) {
        JUnitTestData testMethod = new JUnitTestData(false, false);
        testMethod.setDescription(description);
        if (!testMethodsList.contains(description))
            testMethodsList.add(testMethod);
    }
    
    /**
     * Called when a specific test has been skipped (for whatever reason).
     * 
     * @see org.junit.runner.notification.RunListener#testIgnored(org.junit.runner.Description)
     */
    public void testIgnored(Description description) throws Exception {
        JUnitTestData testMethod = new JUnitTestData(false, false);
        testMethod.setDescription(description);
        if (!testMethodsList.contains(testMethod)) {
            testMethodsList.add(testMethod);
        } else {
            testMethod = testMethodsList.get(testMethodsList.indexOf(testMethod));
        }
        testMethod.setIgnored(true);
    }

    /**
     * Called when a specific test has failed.
     * 
     * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
     */
    public void testFailure(Failure failure) throws Exception {
        JUnitTestData testMethod = new JUnitTestData(false, true);
        testMethod.setDescription(failure.getDescription());
        if (!testMethodsList.contains(testMethod)) {
            testMethodsList.add(testMethod);
        } else {
            testMethod = testMethodsList.get(testMethodsList.indexOf(testMethod));
        }
        testMethod.setFailed(true);
        testMethod.setFailException(failure.getException());
        testMethod.setDescription(failure.getDescription());
    }

    /**
     * Called after a specific test has finished.
     * 
     * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
     */
    public void testFinished(Description description) throws Exception {
    }

    /**
     * Generate tap file for each method
     * 
     * @param result
     */
    protected void generateTapPerMethod(Result result) {
        for (final JUnitTestData testMethod : testMethodsList) {
            final TestResult tapTestResult = TapJUnitUtil
                    .generateTAPTestResult(testMethod, 1, isYaml());

            final TestSet testSet = new TestSet();
            testSet.setPlan(new Plan(1));
            testSet.addTestResult(tapTestResult);

            final String className = TapJUnitUtil.extractClassName(testMethod
                    .getDescription());
            final String methodName = TapJUnitUtil.extractMethodName(testMethod
                    .getDescription());

            final File output = new File(System.getProperty(
                    "tap.junit.results", "target/"), className + "#"
                    + methodName + ".tap");
            tapProducer.dump(testSet, output);
        }
    }

    /**
     * Generate tap file for a class
     * 
     * @param result
     */
    protected void generateTapPerClass(Result result) {
        TestSet testSet = new TestSet();
        File output;
        Integer methodsSizeList = 0;

        String className = "";
        String lastClassName = "";

        for (JUnitTestData testMethod : testMethodsList) {
            className = TapJUnitUtil.extractClassName(testMethod
                    .getDescription());

            if (lastClassName != null && lastClassName.trim().length() > 0
                    && !lastClassName.equals(className)) {
                testSet.setPlan(new Plan(methodsSizeList));
                output = new File(System.getProperty("tap.junit.results",
                        "target/"), lastClassName + ".tap");
                tapProducer.dump(testSet, output);

                testSet = new TestSet();
                methodsSizeList = 0;
            }
            TestResult tapTestResult = TapJUnitUtil.generateTAPTestResult(
                    testMethod, 1, isYaml());
            testSet.addTestResult(tapTestResult);
            methodsSizeList++;

            lastClassName = className;
        }

        testSet.setPlan(new Plan(methodsSizeList));
        output = new File(System.getProperty("tap.junit.results", "target/"),
                className + ".tap");
        tapProducer.dump(testSet, output);
    }

    /**
     * Generate tap file for per suite
     * 
     * @param result
     */
    protected void generateTapPerSuite(Result result) {
        TestSet testSet = new TestSet();
        testSet.setPlan(new Plan(testMethodsList.size()));
        String className = "";

        for (JUnitTestData testMethod : testMethodsList) {
            className = TapJUnitUtil.extractClassName(testMethod
                    .getDescription());

            TestResult tapTestResult = TapJUnitUtil.generateTAPTestResult(
                    testMethod, 1, isYaml());
            testSet.addTestResult(tapTestResult);
        }

        File output = new File(System.getProperty("tap.junit.results",
                "target/"), className + "-SUITE.tap");
        tapProducer.dump(testSet, output);
    }

    /**
     * Set failed info
     * 
     * @param result
     */
    protected void setFailedTestsStatus(Result result) {
        if (result.getFailureCount() > 0) {
            for (Failure f : result.getFailures()) {
                // Change test status to Failed
                for (JUnitTestData testMethod : testMethodsList) {
                    if (testMethod.getDescription().getDisplayName()
                            .equals(f.getTestHeader())) {
                        testMethod.setFailed(true);
                        testMethod.setFailMessage(f.getMessage());
                        testMethod.setFailException(f.getException());
                        testMethod.setFailTrace(f.getTrace());
                    }
                }
            }
        }
    }

}
