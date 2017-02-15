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

package org.tap4j.ext.testng.listener;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tap4j.ext.testng.util.TapTestNGUtil;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.internal.ResultMap;

/**
 * An abstract TestNG test reporter.
 * 
 * @since 1.0
 */
abstract class TapListener extends TestListenerAdapter {

    private final ResultMap resultMap = new ResultMap();

    private final Map<Class<?>, List<ITestResult>> testResultsPerClass
    = new LinkedHashMap<Class<?>, List<ITestResult>>();

    private final Map<ITestNGMethod, List<ITestResult>> testResultsPerMethod
    = new LinkedHashMap<ITestNGMethod, List<ITestResult>>();

    private ITestContext ctx;

    /**
     * TAP Producer.
     */
    private Producer tapProducer = TapProducerFactory.makeTap13YamlProducer();

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.TestListenerAdapter#onStart(org.testng.ITestContext)
     */
    @Override
    public void onStart(ITestContext testContext) {
        this.ctx = testContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.TestListenerAdapter#onTestSuccess(org.testng.ITestResult)
     */
    @Override
    public void onTestSuccess(ITestResult tr) {
        TapTestNGUtil.fillAttributes(tr, ctx);

        resultMap.addResult(tr, tr.getMethod());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.TestListenerAdapter#onTestFailure(org.testng.ITestResult)
     */
    @Override
    public void onTestFailure(ITestResult tr) {
        TapTestNGUtil.fillAttributes(tr, ctx);

        resultMap.addResult(tr, tr.getMethod());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.TestListenerAdapter#onTestSkipped(org.testng.ITestResult)
     */
    @Override
    public void onTestSkipped(ITestResult tr) {
        TapTestNGUtil.fillAttributes(tr, ctx);

        resultMap.addResult(tr, tr.getMethod());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.testng.TestListenerAdapter#onTestFailedButWithinSuccessPercentage
     * (org.testng.ITestResult)
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult tr) {
        TapTestNGUtil.fillAttributes(tr, ctx);

        resultMap.addResult(tr, tr.getMethod());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.TestListenerAdapter#onFinish(org.testng.ITestContext)
     */
    @Override
    public abstract void onFinish(ITestContext testContext);

    /**
     * @return <code>true</code> when output YAML is enabled.
     */
    public abstract boolean isYaml();

    /**
     * Generate TAP file for tests
     * 
     * @param testContext
     */
    protected void generateTAPPerClass(ITestContext testContext) {
        List<ITestResult> testNGTestResults = TapTestNGUtil
                .getTestNGResultsOrderedByExecutionDate(this.resultMap);

        for (ITestResult testResult : testNGTestResults) {
            Class<?> clazz = testResult.getMethod().getRealClass();
            List<ITestResult> testResultsForThisClass = testResultsPerClass
                    .get(clazz);
            if (testResultsForThisClass == null) {
                testResultsForThisClass = new LinkedList<ITestResult>();
                testResultsPerClass.put(clazz, testResultsForThisClass);
            }
            testResultsForThisClass.add(testResult);
        }

        for (Entry<Class<?>, List<ITestResult>> entry : testResultsPerClass.entrySet()) {
            TestSet testSet = new TestSet();

            List<ITestResult> testResults = entry.getValue();

            testSet.setPlan(new Plan(testResults.size()));

            for (ITestResult testResult : testResults) {
                TestResult tapTestResult = TapTestNGUtil.generateTAPTestResult(
                        testResult, testSet.getNumberOfTestResults() + 1,
                        isYaml());
                testSet.addTestResult(tapTestResult);
            }

            File output = new File(testContext.getOutputDirectory(),
                    entry.getKey().getName() + ".tap");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            tapProducer.dump(testSet, output);
        }
    }

    /**
     * Generate a TAP file for every method tested
     * 
     * @param testContext
     */
    protected void generateTAPPerMethod(ITestContext testContext) {
        List<ITestResult> testNGTestResults = TapTestNGUtil
                .getTestNGResultsOrderedByExecutionDate(resultMap);

        for (ITestResult testResult : testNGTestResults) {
            ITestNGMethod method = testResult.getMethod();

            List<ITestResult> testResultsForThisMethod = testResultsPerMethod
                    .get(method);

            if (testResultsForThisMethod == null) {
                testResultsForThisMethod = new LinkedList<ITestResult>();
                testResultsPerMethod.put(method, testResultsForThisMethod);
            }
            testResultsForThisMethod.add(testResult);
        }

        for (Entry<ITestNGMethod, List<ITestResult>> entry : testResultsPerMethod.entrySet()) {
            TestSet testSet = new TestSet();

            List<ITestResult> testResults = entry.getValue();
            testSet.setPlan(new Plan(testResults.size()));

            for (ITestResult testResult : testResults) {
                TestResult tapTestResult = TapTestNGUtil.generateTAPTestResult(
                        testResult, testSet.getNumberOfTestResults() + 1,
                        isYaml());
                testSet.addTestResult(tapTestResult);
            }

            File output = new File(testContext.getOutputDirectory(), entry.getKey()
                    .getTestClass().getName()
                    + "#"
                    + entry.getKey().getMethodName()
                    + ".tap");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            tapProducer.dump(testSet, output);
        }
    }
}
