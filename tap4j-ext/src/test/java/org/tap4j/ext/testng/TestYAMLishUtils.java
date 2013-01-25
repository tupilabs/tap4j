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

package org.tap4j.ext.testng;

import org.tap4j.ext.testng.util.TapTestNGYamlUtil;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;

/**
 * Tests YAMLishUtils class.
 * 
 * @since 1.4.3
 */
public class TestYAMLishUtils {

    ITestResult iTestResult;

    @BeforeClass
    public void setUp() {
        // TBD: generate an iTestResult object with all the information
        // necessary to test the methods from YAMLishUtils class and TAPUtils
        // class
        iTestResult = new TestResult();
        iTestResult.setAttribute("", "");
    }

    @Test
    public void testGetMessage() {
        ITestResult iTestResult = new TestResult();

        String msg = TapTestNGYamlUtil.getMessage(iTestResult);

        Assert.assertNotNull(msg);
    }
}
