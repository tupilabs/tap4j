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

import java.util.Map;

import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.testng.IResultMap;
import org.testng.ITestResult;
import org.testng.internal.ResultMap;

/**
 * @author Cesar Fernandes de Almeida
 * @since 1.4.3
 */
final class JUnitTAPUtils {
	private JUnitTAPUtils() {
		super();
	}

	/**
	 * Generate TAP test result
	 * 
	 * @param testMethod
	 * @param number
	 * @return
	 */
	public static TestResult generateTAPTestResult(JUnitTestData testMethod,
	        Integer number) {
		final TestResult tapTestResult = new TestResult();

		String testResultDescription = generateTAPTestResultDescription(testMethod);
		tapTestResult.setDescription(testResultDescription);

		setTapTestStatus(tapTestResult, testMethod);

		createTestNGYAMLishData(tapTestResult, testMethod);

		return tapTestResult;
	}

	/**
	 * Get the tap test description
	 * 
	 * @param testMethod
	 * @return the tap test description
	 */
	public static String generateTAPTestResultDescription(
	        JUnitTestData testMethod) {
		StringBuilder description = new StringBuilder();

		// An extra space is added before the description by the TAP Representer
		description.append("- ");
		description.append(JUnitYAMLishUtils.extractClassName(testMethod
		        .getDescription()));
		description.append('#');
		description.append(JUnitYAMLishUtils.extractMethodName(testMethod
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
	 * <p>
	 * Inserts TestNG YAMLish diagnostic information into a TAP TestResult.
	 * </p>
	 * 
	 * <p>
	 * For more about TAP YAMLish diagnostic read this <a
	 * href="http://testanything.org/wiki/index.php/YAMLish">Wiki</a>.
	 * </p>
	 * 
	 * @param testResult
	 *            TAP TestResult
	 * @param JUnit40Test
	 *            testMethod with JUnit test info
	 */
	public static void createTestNGYAMLishData(TestResult testResult,
	        JUnitTestData testMethod) {
		final Map<String, Object> yamlish = testResult.getDiagnostic();

		// Root namespace

		createYAMLishMessage(yamlish, testMethod);
		createYAMLishSeverity(yamlish, testMethod);
		createYAMLishSource(yamlish, testMethod);
		createYAMLishDatetime(yamlish);
		createYAMLishFile(yamlish, testMethod);
		createYAMLishLine(yamlish, testMethod);
		createYAMLishName(yamlish, testMethod);
		createYAMLishError(yamlish, testMethod);
		createYAMLishBacktrace(yamlish, testMethod);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishMessage(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String message = JUnitYAMLishUtils.getMessage(testMethod);
		yamlish.put("message", message);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishSeverity(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String severity = JUnitYAMLishUtils.getSeverity(testMethod);
		yamlish.put("severity", severity);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishSource(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String methodName = JUnitYAMLishUtils.extractMethodName(testMethod
		        .getDescription());
		String className = JUnitYAMLishUtils.extractClassName(testMethod
		        .getDescription());

		String source = JUnitYAMLishUtils.getSource(methodName, className);
		yamlish.put("source", source);
	}

	/**
	 * 
	 * @param yamlish
	 */
	public static void createYAMLishDatetime(Map<String, Object> yamlish) {
		String datetime = JUnitYAMLishUtils.getDatetime();
		yamlish.put("datetime", datetime);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishFile(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String file = JUnitYAMLishUtils.getFile(testMethod);
		yamlish.put("file", file);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishLine(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String line = JUnitYAMLishUtils.getLine(testMethod);
		yamlish.put("line", line);
	}

	/**
	 * 
	 * @param yamlish
	 * @param testMethod
	 */
	public static void createYAMLishName(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String name = JUnitYAMLishUtils.getName(testMethod);
		yamlish.put("name", name);
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishError(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		String error = JUnitYAMLishUtils.getError(testMethod);
		yamlish.put("error", error);
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	public static void createYAMLishBacktrace(Map<String, Object> yamlish,
	        JUnitTestData testMethod) {
		Object backtrace = JUnitYAMLishUtils.getBacktrace(testMethod);
		yamlish.put("backtrace", backtrace);
	}

	/**
	 * Adds all ITestResult's inside the map object inside the total one.
	 * 
	 * @param total
	 *            ResultMap that holds the total of IResultMap's.
	 * @param map
	 *            An IResultMap object.
	 */
	public static void addAll(ResultMap total, IResultMap map) {
		for (ITestResult testResult : map.getAllResults()) {
			total.addResult(testResult, testResult.getMethod());
		}
	}
}
