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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.tap4j.model.TestResult;
import org.tap4j.util.StatusValues;

/**
 * Test TAP 13 YAML representer.
 *
 * @since 4.0
 */
public class TestTap13YamlRepresenter {

    @Test
    public void testOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(100);
        options.setPrintDiagnostics(true);
        Tap13Representer repr = new Tap13Representer(options);
        assertNotNull(repr.getOptions());
        assertEquals(options, repr.getOptions());
        assertEquals(100, options.getIndent());
        assertTrue(options.isPrintDiagnostics());
    }

    @Test
    public void printDiagnosticNull() {
        DumperOptions options = new DumperOptions();
        options.setPrintDiagnostics(true);
        Tap13Representer repr = new Tap13Representer(options);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(null);
        repr.printDiagnostic(pw, tr);
        assertEquals("", sw.toString());
    }

    @Test
    public void printDiagnosticEmpty() {
        Map<String, Object> diagnostic = new HashMap<>();
        DumperOptions options = new DumperOptions();
        options.setPrintDiagnostics(true);
        Tap13Representer repr = new Tap13Representer(options);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(diagnostic);
        repr.printDiagnostic(pw, tr);
        assertEquals("", sw.toString());
    }

    @Test
    public void printDiagnostic() {
        Map<String, Object> diagnostic = new LinkedHashMap<>();
        diagnostic.put("name", "Ayrton");
        diagnostic.put("surname", "Senna");
        DumperOptions options = new DumperOptions();
        options.setPrintDiagnostics(true);
        Tap13Representer repr = new Tap13Representer(options);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(diagnostic);
        repr.printDiagnostic(pw, tr);
        assertTrue(sw.toString().contains("---\n  name: Ayrton\n  surname: Senna"));
    }

}
