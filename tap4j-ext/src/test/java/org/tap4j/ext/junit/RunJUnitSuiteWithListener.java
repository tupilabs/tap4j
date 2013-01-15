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

package org.tap4j.ext.junit;

import junit.framework.TestSuite;

import org.junit.runner.JUnitCore;

/**
 * Class for creating a JUnit suite runner and add the TAP listener
 * 
 * @author Cesar Fernandes de Almeida
 * @since 2.01
 */
public class RunJUnitSuiteWithListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Create a JUnit suite
        TestSuite suite = new TestSuite();

        // Add every test class you want to the suite
        suite.addTestSuite(TestTap13JUnit1.class);
        suite.addTestSuite(TestTap13JUnit2.class);

        // Instantiate a JUniteCore object
        JUnitCore core = new JUnitCore();

        // Add TAP Reporter Listener to the core object executor
        core.addListener(new JUnitTestTapReporter());

        // Run the test suite
        core.run(suite);
    }
}
