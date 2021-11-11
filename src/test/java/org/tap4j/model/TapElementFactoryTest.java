/*
 * The MIT License
 *
 * Copyright (c) 2016 tap4j team (see AUTHORS)
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
package org.tap4j.model;

import org.junit.Test;
import static org.junit.Assert.*;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

/**
 * Tests for {@link TapElementFactory}.
 * @since 4.2.0
 */
public class TapElementFactoryTest {

    /**
     * Test footer.
     */
    @Test
    public void testFooter() {
        Footer footer = (Footer) TapElementFactory.createTapElement("TAP done.");
        assertEquals(0, footer.getIndentation());
        assertEquals("done.", footer.getText());

        footer = (Footer) TapElementFactory.createTapElement("  TAP done.");
        assertEquals("done.", footer.getText());
        assertEquals(2, footer.getIndentation());

        footer = (Footer) TapElementFactory.createTapElement("    TAP done.# hopefully");
        assertEquals("done.", footer.getText());
        assertEquals(4, footer.getIndentation());
        assertEquals("hopefully", footer.getComment().getText());
    }

    /**
     * Test header.
     */
    @Test
    public void testHeader() {
        Header header = (Header) TapElementFactory.createTapElement("TAP version 13");
        assertEquals(0, header.getIndentation());
        assertEquals(13, header.getVersion().intValue());

        header = (Header) TapElementFactory.createTapElement("TAP version 13 #commented");
        assertEquals(0, header.getIndentation());
        assertEquals("commented", header.getComment().getText());
        assertEquals(13, header.getVersion().intValue());

        header = (Header) TapElementFactory.createTapElement("    TAP version 12");
        assertEquals(4, header.getIndentation());
        assertEquals(12, header.getVersion().intValue());
    }

    /**
     * Test plan.
     */
    @Test
    public void testPlan() {
        Plan plan = (Plan) TapElementFactory.createTapElement("1..2");
        assertEquals(0, plan.getIndentation());
        assertEquals(1, plan.getInitialTestNumber().intValue());
        assertEquals(2, plan.getLastTestNumber().intValue());

        plan = (Plan) TapElementFactory.createTapElement("  1..0");
        assertEquals(2, plan.getIndentation());
        assertEquals(1, plan.getInitialTestNumber().intValue());
        assertEquals(0, plan.getLastTestNumber().intValue());

        plan = (Plan) TapElementFactory.createTapElement("  1..0 # bummer");
        assertEquals(2, plan.getIndentation());
        assertEquals(1, plan.getInitialTestNumber().intValue());
        assertEquals(0, plan.getLastTestNumber().intValue());
        assertEquals("bummer", plan.getComment().getText());

        plan = (Plan) TapElementFactory.createTapElement("   12..50 # SKIP to avoid failures");
        assertEquals(3, plan.getIndentation());
        assertEquals(12, plan.getInitialTestNumber().intValue());
        assertEquals(50, plan.getLastTestNumber().intValue());
        assertEquals("to avoid failures", plan.getSkip().getReason());
    }

    @Test
    public void testBailOut() {
        BailOut bailOut = (BailOut) TapElementFactory.createTapElement("Bail out!");
        assertEquals(0, bailOut.getIndentation());

        bailOut = (BailOut) TapElementFactory.createTapElement("    Bail out!");
        assertEquals(4, bailOut.getIndentation());

        bailOut = (BailOut) TapElementFactory.createTapElement("  Bail out! Now!");
        assertEquals(2, bailOut.getIndentation());
        assertEquals("Now!", bailOut.getReason());
    }

    @Test
    public void testTestResult() {
        TestResult testResult = (TestResult) TapElementFactory.createTapElement("ok 365");
        assertEquals(0, testResult.getIndentation());
        assertEquals(365, testResult.getTestNumber().intValue());
        assertEquals(StatusValues.OK, testResult.getStatus());

        testResult = (TestResult) TapElementFactory.createTapElement(" not ok 42");
        assertEquals(1, testResult.getIndentation());
        assertEquals(42, testResult.getTestNumber().intValue());
        assertNull(testResult.getDirective());
        assertEquals(StatusValues.NOT_OK, testResult.getStatus());

        testResult = (TestResult) TapElementFactory.createTapElement("  ok 15 #SKIP");
        assertEquals(2, testResult.getIndentation());
        assertEquals(15, testResult.getTestNumber().intValue());
        assertEquals(DirectiveValues.SKIP, testResult.getDirective().getDirectiveValue());
        assertEquals(StatusValues.OK, testResult.getStatus());

        testResult = (TestResult) TapElementFactory.createTapElement("  ok 15 #TODO fixme");
        assertEquals(2, testResult.getIndentation());
        assertEquals(15, testResult.getTestNumber().intValue());
        assertEquals(DirectiveValues.TODO, testResult.getDirective().getDirectiveValue());
        assertEquals("fixme", testResult.getDirective().getReason());
        assertEquals(StatusValues.OK, testResult.getStatus());

        testResult = (TestResult) TapElementFactory.createTapElement("  ok 22 bummer");
        assertEquals(2, testResult.getIndentation());
        assertEquals(22, testResult.getTestNumber().intValue());
        assertEquals("bummer", testResult.getDescription());
        assertEquals(StatusValues.OK, testResult.getStatus());

        testResult = (TestResult) TapElementFactory.createTapElement("   ok 33 #comment");
        assertEquals(3, testResult.getIndentation());
        assertEquals(33, testResult.getTestNumber().intValue());
        assertEquals("comment", testResult.getComment().getText());
        assertEquals(StatusValues.OK, testResult.getStatus());
    }

    @Test
    public void testComment() {
        Comment comment = (Comment) TapElementFactory.createTapElement("#bummer");
        assertEquals(0, comment.getIndentation());
        assertEquals("bummer", comment.getText());

        comment = (Comment) TapElementFactory.createTapElement("  #one two");
        assertEquals(2, comment.getIndentation());
        assertEquals("one two", comment.getText());
    }
}
