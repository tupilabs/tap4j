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

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DumperOptions.
 *
 * @since 4.0
 */
public class TestDumperOptions {

    private DumperOptions options = null;
    
    @Before
    public void setUp() {
        options = new DumperOptions();
    }
    
    @Test
    public void testDefaultOptions() {
        assertEquals(0, options.getIndent());
        assertEquals(4, options.getSpaces());
        assertEquals(true, options.isAllowEmptyTestPlan());
        assertEquals(false, options.isPrintDiagnostics());
        assertEquals(true, options.isPrintSubtests());
        assertEquals(Charset.defaultCharset().toString(), options.getCharset());
    }
    
    @Test
    public void testChangingOptions() {
        int indent = 1;
        int spaces = 2;
        boolean allowEmptyTestPlan = false;
        boolean printDiagnostics = true;
        boolean printSubtests = false;
        String charset = "ISO-8859-1";
        options.setIndent(indent);
        options.setSpaces(spaces);
        options.setAllowEmptyTestPlan(allowEmptyTestPlan);
        options.setPrintDiagnostics(printDiagnostics);
        options.setPrintSubtests(printSubtests);
        options.setCharset(charset);
        assertEquals(indent, options.getIndent());
        assertEquals(spaces, options.getSpaces());
        assertEquals(allowEmptyTestPlan, options.isAllowEmptyTestPlan());
        assertEquals(printDiagnostics, options.isPrintDiagnostics());
        assertEquals(printSubtests, options.isPrintSubtests());
        assertEquals(charset, options.getCharset());
    }
    
}
