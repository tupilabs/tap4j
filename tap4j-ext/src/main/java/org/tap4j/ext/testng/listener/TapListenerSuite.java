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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tap4j.ext.testng.util.TapTestNGUtil;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducer;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/**
 * A TestNG suite TAP reporter.
 * 
 * @since 1.0
 */
public class TapListenerSuite implements IReporter {

    private final Map<Class<?>, List<ITestResult>> testResultsPerSuite
        = new LinkedHashMap<Class<?>, List<ITestResult>>();

    private final Map<String, List<ITestResult>> testResultsPerGroup
        = new LinkedHashMap<String, List<ITestResult>>();

    // private final Map<ITestNGMethod, List<ITestResult>> testResultsPerMethod
    // = new LinkedHashMap<ITestNGMethod, List<ITestResult>>();

    /**
     * TAP Producer.
     */
    private Producer tapProducer = new TapProducer();

    /**
     * TAP Test Set
     */
    private TestSet testSet;

    /*
     * (non-Javadoc)
     * 
     * @see org.testng.IReporter#generateReport(java.util.List, java.util.List,
     * java.lang.String)
     */
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
            String outputDirectory) {
        this.generateTAPPerSuite(xmlSuites, suites, outputDirectory);

        this.generateTAPPerGroup(xmlSuites, suites, outputDirectory);
    }

    /**
     * Generate a TAP file for every suite tested
     * 
     * @param xmlSuites
     * @param suites
     * @param outputDirectory
     */
    protected void generateTAPPerSuite(List<XmlSuite> xmlSuites,
            List<ISuite> suites, String outputDirectory) {
        for (ISuite suite : suites) {
            testSet = new TestSet();

            Set<Class<?>> testResultsSet = this
                    .getTestResultsSetPerSuite(suite);

            Integer totalTestResults = this
                    .getTotalTestResultsByTestSuite(testResultsSet);

            testSet.setPlan(new Plan(totalTestResults));

            for (Class<?> testResultClass : testResultsSet) {
                List<ITestResult> testResults = testResultsPerSuite
                        .get(testResultClass);

                for (ITestResult testResult : testResults) {
                    TestResult tapTestResult = TapTestNGUtil
                            .generateTAPTestResult(testResult,
                                    testSet.getNumberOfTestResults() + 1,
                                    isYaml());
                    testSet.addTestResult(tapTestResult);
                }
            }

            File output = new File(outputDirectory, suite.getName() + ".tap");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            tapProducer.dump(testSet, output);
        }
    }

    /**
     * Generate a TAP file for every group tested
     * 
     * @param xmlSuites
     * @param suites
     * @param outputDirectory
     */
    protected void generateTAPPerGroup(List<XmlSuite> xmlSuites,
            List<ISuite> suites, String outputDirectory) {
        for (ISuite suite : suites) {
            Map<String, Collection<ITestNGMethod>> groups = suite
                    .getMethodsByGroups();

            this.populateTestResultsPerGroupMap(suite, groups);

            if (groups.size() > 0) {
                String[] groupNames = groups.keySet().toArray(
                        new String[groups.size()]);
                Arrays.sort(groupNames);

                for (String group : groupNames) {
                    if (group != null && group.trim().length() > 0) {
                        List<ITestResult> groupTestResults = testResultsPerGroup
                                .get(group);

                        if (groupTestResults != null) {
                            final Integer totalTestResultsByGroup = groupTestResults
                                    .size();

                            testSet = new TestSet();

                            testSet.setPlan(new Plan(totalTestResultsByGroup));

                            for (ITestResult testResult : groupTestResults) {
                                TestResult tapTestResult = TapTestNGUtil
                                        .generateTAPTestResult(
                                                testResult,
                                                testSet.getNumberOfTestResults() + 1,
                                                isYaml());
                                testSet.addTestResult(tapTestResult);
                            }

                            File output = new File(outputDirectory, group
                                    + ".tap");
                            if (!output.getParentFile().exists()) {
                                output.getParentFile().mkdirs();
                            }
                            tapProducer.dump(testSet, output);
                        }
                    }
                }
            }
        }
    }

    /**
     * Get a Set of test Results for Suites by a given ISuite
     * 
     * @param suite
     * @return Set of Classes for a Suite
     */
    protected Set<Class<?>> getTestResultsSetPerSuite(ISuite suite) {
        XmlSuite xmlSuite = suite.getXmlSuite();

        // Popula o mapa testResultsPerSuite com uma classe para cada suite com
        // seus resultados
        this.generateClasses(xmlSuite, suite);

        return testResultsPerSuite.keySet();
    }

    /**
     * Populate a Map of test Results for Groups by a given ISuite
     * 
     * @param suite
     * @param groups
     */
    protected void populateTestResultsPerGroupMap(ISuite suite,
            Map<String, Collection<ITestNGMethod>> groups) {
        XmlSuite xmlSuite = suite.getXmlSuite();

        // Popula o mapa testResultsPerGroup com uma String para cada grupo com
        // seus resultados
        this.generateResultsMapForGroups(xmlSuite, suite, groups);
    }

    /**
     * Get total results from a test suite
     * 
     * @param keySet
     * @return Total Results
     */
    public Integer getTotalTestResultsByTestSuite(Set<Class<?>> keySet) {
        Integer totalTestResults = 0;
        for (Class<?> clazz : keySet) {
            List<ITestResult> testResults = testResultsPerSuite.get(clazz);
            totalTestResults += testResults.size();
        }
        return totalTestResults;
    }

    /**
     * Populate a List of ITestResults for every test Class in a test Suite
     * 
     * @param xmlSuite
     * @param suite
     */
    public void generateClasses(XmlSuite xmlSuite, ISuite suite) {
        if (suite.getResults().size() > 0) {
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                List<ITestResult> testResults = TapTestNGUtil
                        .getTestNGResultsOrderedByExecutionDate(suiteResult
                                .getTestContext());

                for (ITestResult testResult : testResults) {
                    Class<?> clazz = testResult.getMethod().getRealClass();
                    List<ITestResult> testResultsForThisClass = testResultsPerSuite
                            .get(clazz);

                    if (testResultsForThisClass == null) {
                        testResultsForThisClass = new LinkedList<ITestResult>();
                        testResultsPerSuite.put(clazz, testResultsForThisClass);
                    }
                    testResultsForThisClass.add(testResult);
                }
            }
        }
    }

    /**
     * Generate the map for the groups
     * 
     * @param xmlSuite
     * @param suite
     * @param groups
     */
    protected void generateResultsMapForGroups(XmlSuite xmlSuite, ISuite suite,
            Map<String, Collection<ITestNGMethod>> groups) {
        if (suite.getResults().size() > 0) {
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                List<ITestResult> testResults = TapTestNGUtil
                        .getTestNGResultsOrderedByExecutionDate(suiteResult
                                .getTestContext());

                for (ITestResult testResult : testResults) {
                    ITestNGMethod method = testResult.getMethod();

                    String[] groupsNm = findInWhatGroupsMethodIs(method, groups);

                    for (String gpNm : groupsNm) {
                        if (gpNm != null && gpNm.trim().length() > 0) {
                            List<ITestResult> testResultsForThisGroup = testResultsPerGroup
                                    .get(gpNm);

                            if (testResultsForThisGroup == null) {
                                testResultsForThisGroup = new LinkedList<ITestResult>();
                                testResultsPerGroup.put(gpNm,
                                        testResultsForThisGroup);
                            }
                            testResultsForThisGroup.add(testResult);
                        }
                    }
                }
            }
        }
    }

    /**
     * Get all groups names that a Test Method is inclueded in
     * 
     * @param methodToFind
     * @param groups
     */
    protected String[] findInWhatGroupsMethodIs(ITestNGMethod methodToFind,
            Map<String, Collection<ITestNGMethod>> groups) {
        String[] groupsFound = new String[groups.keySet().size()];
        int cont = 0;
        for (Map.Entry<String, Collection<ITestNGMethod>> grupo : groups
                .entrySet()) {
            for (ITestNGMethod method : grupo.getValue()) {
                if (method.equals(methodToFind)
                        && method.getRealClass().equals(
                                methodToFind.getRealClass())) {
                    groupsFound[cont++] = grupo.getKey();
                }
            }
        }
        return groupsFound;
    }

    /**
     * @return <code>true</code> when output YAML is enabled.
     */
    public boolean isYaml() {
        return false;
    }

}
