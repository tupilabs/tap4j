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
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.tap4j.model.TestSet;

/**
 * Tests for TAP 13 Parser.
 * @since 4.0
 */
public class TestTap13Parser {

    private Tap13Parser parser;

    @Before
    public void setUp() {
        parser = new Tap13Parser();
    }

    @Test
    public void testTestResultWithEmptyComment() {
        String tap = "1..1\n" +
                "ok 1 #";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(0, testSet.getTestResult(1).getComments().size());
    }

    @Test
    public void testTestResultWithComment() {
        String tap = "1..1\n" +
                "ok 1 # a comment";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals("a comment", testSet.getTestResult(1).getComments().get(0).getText());
    }

    @Test
    public void testFooterWithAnEmptyComment() {
        String tap = "1..1\n" +
                "ok 1\n" +
                "TAP end #";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(null, testSet.getFooter().getComment());
    }

    @Test
    public void testFooterWithComment() {
        String tap = "1..1\n" +
                "ok 1\n" +
                "TAP end # a comment";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals("a comment", testSet.getFooter().getComment().getText());
    }

    @Test
    public void testHeader() {
        String tap = "TAP version 13\n" +
                "1..1\n" +
                "ok 1\n" +
                "TAP end # a comment";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(new Integer(13), testSet.getHeader().getVersion());
    }

    @Test(expected=ParserException.class)
    public void testHeaderDuplicated() {
        String tap = "TAP version 13\n" +
                "TAP version 13\n" +
                "1..1\n" +
                "ok 1\n" +
                "TAP end # a comment";
        parser.parseTapStream(tap);
        fail("Not supposed to get here");
    }

    @Test
    public void testHeaderWithEmptyComment() {
        String tap = "TAP version 13 #\n" +
                "1..1\n" +
                "ok 1";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(null, testSet.getHeader().getComment());
    }

    @Test
    public void testHeaderWithComment() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1\n" +
                "ok 1\n" +
                "TAP end # a comment";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals("a comment", testSet.getHeader().getComment().getText());
    }

    @Test
    public void testPlan() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1\n" +
                "ok 1";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(new Integer(1), testSet.getPlan().getInitialTestNumber());
    }

    @Test(expected=ParserException.class)
    public void testPlanDuplicated() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1\n" +
                "1..2\n" +
                "ok 1";
        parser.parseTapStream(tap);
        fail("Not supposed to get here");
    }

    @Test
    public void testPlanSkip() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1 # skip betsu ni\n" +
                "ok 1";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals("betsu ni", testSet.getPlan().getSkip().getReason());
    }

    @Test
    public void testPlanWithEmptyComment() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1 #\n" +
                "ok 1";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals(null, testSet.getPlan().getComment());
    }

    @Test
    public void testPlanWithComment() {
        String tap = "TAP version 13 # a comment\n" +
                "1..1 # a comment\n" +
                "ok 1";
        TestSet testSet = parser.parseTapStream(tap);
        assertEquals("a comment", testSet.getPlan().getComment().getText());
    }

    @Test(expected=ParserException.class)
    public void notExistentFile() {
        parser.parseFile(new File(""+System.currentTimeMillis()+System.nanoTime()));
    }

}
