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

import org.junit.runner.Description;

/**
 * JUnit test data.
 * 
 * @since 1.4.3
 */
public class JUnitTestData {

    private Description description;

    private Boolean ignored;

    private Boolean failed;

    private String failMessage;

    private String failTrace;

    private Throwable failException;

    public JUnitTestData(Boolean ignored, Boolean failed) {
        this.ignored = ignored;
        this.failed = failed;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(Boolean ignored) {
        this.ignored = ignored;
    }

    public Boolean isFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getFailTrace() {
        return failTrace;
    }

    public void setFailTrace(String failTrace) {
        this.failTrace = failTrace;
    }

    public Throwable getFailException() {
        return failException;
    }

    public void setFailException(Throwable failException) {
        this.failException = failException;
    }
}