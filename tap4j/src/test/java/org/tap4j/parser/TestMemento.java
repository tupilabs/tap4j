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
package org.tap4j.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tap4j.model.TapElement;
import org.tap4j.model.Text;

/**
 * Tests for memento class.
 */
public class TestMemento {

    private static StreamStatus memento = null;
    
    @BeforeClass
    public static void setUp() {
        memento = new StreamStatus();
    }
    
    @Test
    public void testMemento() {
        assertTrue(memento.isFirstLine());
        assertFalse(memento.isPlanBeforeTestResult());
        assertNull(memento.getLastLine());
        assertNull(memento.getLastParsedElement());
        assertEquals(0, memento.getIndentationLevel());
        assertFalse(memento.isInYaml());
        assertTrue(memento.getDiagnosticBuffer().toString().length() == 0);
        
        int currentIndentationLevel = 100;
        int baseIndentationLevel = 50;
        boolean currentlyInSubtest = true;
        boolean currentlyInYaml = true;
        boolean firstLine = false;
        String lastLine = "nani";
        TapElement lastParsedElement = new Text("nani nani");
        boolean planBeforeTestResult = true;
        
        memento.setIndentationLevel(baseIndentationLevel);
        memento.setInYaml(currentlyInYaml);
        memento.setFirstLine(firstLine);
        memento.setLastLine(lastLine);
        memento.setLastParsedElement(lastParsedElement);
        memento.setPlanBeforeTestResult(planBeforeTestResult);
        
        assertEquals(firstLine, memento.isFirstLine());
        assertEquals(planBeforeTestResult, memento.isPlanBeforeTestResult());
        assertEquals(lastLine, memento.getLastLine());
        assertEquals(lastParsedElement, memento.getLastParsedElement());
        assertEquals(baseIndentationLevel, memento.getIndentationLevel());
        assertEquals(currentlyInYaml, memento.isInYaml());
    }
}
