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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @author Cesar Fernandes de Almeida
 * @since 03/01/2011
 */
public class SuiteTAPReporter implements IReporter {
    private final Map<Class<?>, List<ITestResult>> testResultsPerSuite = new LinkedHashMap<Class<?>, List<ITestResult>>();

    private final Map<String, List<ITestResult>> testResultsPerGroup = new LinkedHashMap<String, List<ITestResult>>();

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
                    TestResult tapTestResult = TestNGTAPUtils
                            .generateTAPTestResult(testResult,
                                    testSet.getNumberOfTestResults() + 1);
                    testSet.addTestResult(tapTestResult);
                }
            }

            File output = new File(outputDirectory, suite.getName() + ".tap");
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
                    if (StringUtils.isNotEmpty(group)) {
                        List<ITestResult> groupTestResults = testResultsPerGroup
                                .get(group);

                        if (groupTestResults != null) {
                            final Integer totalTestResultsByGroup = groupTestResults
                                    .size();

                            testSet = new TestSet();

                            testSet.setPlan(new Plan(totalTestResultsByGroup));

                            for (ITestResult testResult : groupTestResults) {
                                TestResult tapTestResult = TestNGTAPUtils
                                        .generateTAPTestResult(
                                                testResult,
                                                testSet.getNumberOfTestResults() + 1);
                                testSet.addTestResult(tapTestResult);
                            }

                            File output = new File(outputDirectory, group
                                    + ".tap");
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
                List<ITestResult> testResults = TestNGTAPUtils
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
     * Generate the results map for the groups
     * 
     * @param xmlSuite
     * @param suite
     * @param groups
     */
    protected void generateResultsMapForGroups(XmlSuite xmlSuite, ISuite suite,
            Map<String, Collection<ITestNGMethod>> groups) {
        if (suite.getResults().size() > 0) {
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                List<ITestResult> testResults = TestNGTAPUtils
                        .getTestNGResultsOrderedByExecutionDate(suiteResult
                                .getTestContext());

                for (ITestResult testResult : testResults) {
                    ITestNGMethod method = testResult.getMethod();

                    String[] groupsNm = findInWhatGroupsMethodIs(method, groups);

                    for (String gpNm : groupsNm) {
                        if (StringUtils.isNotEmpty(gpNm)) {
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
}

class ExecutionDateCompator implements Comparator<ITestResult>, Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(ITestResult o1, ITestResult o2) {
        if (o1.getStartMillis() > o2.getStartMillis()) {
            return 1;
        }
        return -1;
    }
}
