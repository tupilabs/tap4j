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
package org.tap4j.representer;

/**
 * Options used by a representer.
 * 
 * @since 0.1
 */
public class DumperOptions {

    private boolean printDiagnostics = Boolean.TRUE;

    private boolean printSubtests = Boolean.TRUE;

    private boolean allowEmptyTestPlan = Boolean.TRUE;

    private int indent = 0;

    private int spaces = 4;

    public DumperOptions() {
    }

    /**
     * @return the printDiagnostics
     */
    public boolean isPrintDiagnostics() {
        return printDiagnostics;
    }

    /**
     * @param printDiagnostics the printDiagnostics to set
     */
    public void setPrintDiagnostics(boolean printDiagnostics) {
        this.printDiagnostics = printDiagnostics;
    }

    /**
     * @return the printSubtests
     */
    public boolean isPrintSubtests() {
        return printSubtests;
    }

    /**
     * @param printSubtests the printSubtests to set
     */
    public void setPrintSubtests(boolean printSubtests) {
        this.printSubtests = printSubtests;
    }

    /**
     * @param indent the indent to set
     */
    public void setIndent(int indent) {
        this.indent = indent;
    }

    /**
     * @return the indent
     */
    public int getIndent() {
        return indent;
    }

    /**
     * @param spaces the spaces to set
     */
    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }

    /**
     * @return the spaces
     */
    public int getSpaces() {
        return spaces;
    }

    /**
     * @param allowEmptyTestPlan the allowEmptyTestPlan to set
     */
    public void setAllowEmptyTestPlan(boolean allowEmptyTestPlan) {
        this.allowEmptyTestPlan = allowEmptyTestPlan;
    }

    /**
     * @return the allowEmptyTestPlan
     */
    public boolean isAllowEmptyTestPlan() {
        return allowEmptyTestPlan;
    }

}
