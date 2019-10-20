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

import java.util.Map;

import org.tap4j.model.TestResult;

/**
 * JUnit YAMLish utility class.
 * 
 * @since 1.4.3
 */
public final class TapJUnitYamlUtil {

    /**
     * Default hidden constructor.
     */
    private TapJUnitYamlUtil() {
        super();
    }

    /**
     * <p>
     * Inserts JUnit YAMLish diagnostic information into a TAP TestResult.
     * </p>
     * <p>
     * For more about TAP YAMLish diagnostic read this <a
     * href="http://testanything.org/wiki/index.php/YAMLish">Wiki</a>.
     * </p>
     *
     * @param testResult TAP TestResult
     * @param message
     * @param severity
     * @param source
     * @param datetime
     * @param file
     * @param line
     * @param name
     * @param error
     * @param backtrace
     */
    public static void createJUnitYAMLishData(TestResult testResult,
            String message, String severity, String source, String datetime,
            String file, String line, String name, String error,
            String backtrace) {
        final Map<String, Object> yamlish = testResult.getDiagnostic();

        // Root namespace

        createYAMLishMessage(yamlish, message);
        createYAMLishSeverity(yamlish, severity);
        createYAMLishSource(yamlish, source);
        createYAMLishDatetime(yamlish, datetime);
        createYAMLishFile(yamlish, file);
        createYAMLishLine(yamlish, line);
        createYAMLishName(yamlish, name);
        createYAMLishError(yamlish, error);
        createYAMLishBacktrace(yamlish, backtrace);
    }

    /**
     * @param yamlish YAMLish
     * @param message
     */
    public static void createYAMLishMessage(Map<String, Object> yamlish, String message) {
        yamlish.put("message", message);
    }

    /**
     * @param yamlish YAMLish
     * @param severity
     */
    public static void createYAMLishSeverity(Map<String, Object> yamlish, String severity) {
        yamlish.put("severity", severity);
    }

    /**
     * @param yamlish YAMLish
     * @param source
     */
    public static void createYAMLishSource(Map<String, Object> yamlish, String source) {
        yamlish.put("source", source);
    }

    /**
     * @param yamlish YAMLish
     * @param datetime
     */
    public static void createYAMLishDatetime(Map<String, Object> yamlish, String datetime) {
        yamlish.put("datetime", datetime);
    }

    /**
     * @param yamlish YAMLish
     * @param file
     */
    public static void createYAMLishFile(Map<String, Object> yamlish, String file) {
        yamlish.put("file", file);
    }

    /**
     * @param yamlish YAMLish
     * @param line
     */
    public static void createYAMLishLine(Map<String, Object> yamlish, String line) {
        yamlish.put("line", line);
    }

    /**
     * @param yamlish YAMLish
     * @param name
     */
    public static void createYAMLishName(Map<String, Object> yamlish, String name) {
        yamlish.put("name", name);
    }

    /**
     * @param yamlish YAMLish
     * @param error
     */
    public static void createYAMLishError(Map<String, Object> yamlish, String error) {
        yamlish.put("error", error);
    }

    /**
     * @param yamlish YAMLish
     * @param backtrace
     */
    public static void createYAMLishBacktrace(Map<String, Object> yamlish, Object backtrace) {
        yamlish.put("backtrace", backtrace);
    }

}
