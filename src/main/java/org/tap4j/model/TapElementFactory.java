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

import java.util.regex.Matcher;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

/**
 * Factory to produce {@link TapElement}s from given textual source.
 * @since 4.2.0
 */
public final class TapElementFactory {

    /**
     * Hidden constructor.
     */
    private TapElementFactory() {
        // prevent instantiation
    }

    /**
     * Create a text element. This element is created when none of the other elements have matched the TAP stream line.
     * @param tapLine TAP stream line
     * @return a {@link Text} TAP element
     */
    public static Text createTextElement(String tapLine) {

        Matcher m = Patterns.TEXT_PATTERN.matcher(tapLine);

        if (m.matches()) {
            Text result = new Text(tapLine);
            result.setIndentationString(m.group(1));
            result.indentation = m.group(1).length();
            return result;
        }

        return null;
    }

    /**
     * Create a {@link TapElement} given a TAP stream line. If any TAP element matches the line, it is returned,
     * {@code null} is returned otherwise.
     * @param tapLine TAP stream line
     * @return a {@link TapElement} created with from the given TAP stream line, or {@code null} if not a valid token
     */
    public static TapElement createTapElement(String tapLine) {

        Matcher m;

        m = Patterns.COMMENT_PATTERN.matcher(tapLine);
        if (m.matches()) {
            Comment comment = new Comment(m.group(2), false);
            comment.indentation = m.group(1).length();
            return comment;
        }

        m = Patterns.HEADER_PATTERN.matcher(tapLine);
        if (m.matches()) {
            Header header = new Header(Integer.parseInt(m.group(2)));
            header.indentation = m.group(1).length();
            addComment(header, m.group(4));
            return header;
        }

        m = Patterns.FOOTER_PATTERN.matcher(tapLine);
        if (m.matches()) {
            Footer footer = new Footer(m.group(2));
            addComment(footer, m.group(4));
            footer.indentation = m.group(1).length();
            return footer;
        }

        m = Patterns.PLAN_PATTERN.matcher(tapLine);
        if (m.matches()) {
            String skip = m.group(7);
            String comment = m.group(9);
            SkipPlan skipPlan = skip != null && skip.trim().length() > 0 ? new SkipPlan(skip) : null;
            Plan plan = new Plan(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(4)), skipPlan);
            addComment(plan, comment);
            plan.indentation = m.group(1).length();
            return plan;
        }

        m = Patterns.BAIL_OUT_PATTERN.matcher(tapLine);
        if (m.matches()) {
            String reason = m.group(2);
            String comment = m.group(4);
            BailOut bailOut = new BailOut(reason);
            addComment(bailOut, comment);
            bailOut.indentation = m.group(1).length();
            return bailOut;
        }

        m = Patterns.TEST_RESULT_PATTERN.matcher(tapLine);
        if (m.matches()) {
            String testNumberText = m.group(3);
            int testNumber = 0;
            if (testNumberText != null && !testNumberText.trim().equals("")) {
                testNumber = Integer.parseInt(testNumberText);
            }
            TestResult testResult = new TestResult(StatusValues.get(m.group(2)), testNumber);
            String comment = m.group(9);
            if (comment != null && comment.trim().length() > 0) {
                final Comment c = new Comment(comment, true);
                testResult.setComment(c);
                testResult.addComment(c);
            }
            testResult.setDescription(m.group(4));
            DirectiveValues directive = DirectiveValues.get(m.group(6));
            if (directive != null) {
                testResult.setDirective(new Directive(directive, m.group(7)));
            }
            testResult.indentation = m.group(1).length();
            return testResult;
        }

        return null;
    }

    /**
     * Add a comment to a {@link TapElement}, as long as the comment is not empty.
     * @param element TAP element
     * @param comment a comment
     */
    public static void addComment(TapElement element, String comment) {
        if (comment != null && comment.trim().length() > 0) {
            element.setComment(new Comment(comment, true));
        }
    }
}
