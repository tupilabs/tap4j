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
package org.tap4j.parser.issue3525607;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tap4j.BaseTapTest;
import org.tap4j.model.Comment;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;

/**
 * Tests for issue 3525607
 *
 * @since 3.0
 */
public class TestIssue3525607 extends BaseTapTest {

    @Test
    public void testTestResultWithCommentDiagnostics() {
        final String tap = "1..2\n" + "ok 1 - OK\n" + "# No errors found\n"
                + "not ok 2\n" + "# Invalid stream character\n"
                + "# Missing end transmission signal\n"
                + "# Aborting mission!\n" + "ok";
        final TestSet testSet = getConsumer().load(tap);
        assertEquals("No errors found",
                testSet.getTestResult(1).getComments().get(0).getText());
        final String expected = "Invalid stream character\n"
                + "Missing end transmission signal\n" + "Aborting mission!\n";
        final StringBuilder actualCommentText = new StringBuilder();
        final TestResult testResult = testSet.getTestResult(2);
        final List<Comment> comments = testResult.getComments();
        for (Comment comment : comments) {
            actualCommentText.append(comment.getText());
            actualCommentText.append("\n");
        }
        assertEquals(expected, actualCommentText.toString());
    }

}
