/*
 * The MIT License Copyright (c) <2010> <tap4j> Permission is hereby granted,
 * free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.tap4j.model;

import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests Test Results and Directives/Status.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestTAPTestResult {

    protected TestResult okTestResult = null;

    protected TestResult notOkTestResult = null;

    protected TestResult okTestResultSkip = null;

    @BeforeTest
    public void setUp() {
        okTestResult = new TestResult(StatusValues.OK, 1);
        okTestResult.setDescription("- First test");

        notOkTestResult = new TestResult();
        notOkTestResult.setStatus(StatusValues.NOT_OK);
        notOkTestResult.setTestNumber(2);

        okTestResultSkip = new TestResult(StatusValues.NOT_OK, 3);

        Directive skipDirective = new Directive(DirectiveValues.SKIP,
                                                "Skip it until next release of the produce.");
        okTestResultSkip.setDirective(skipDirective);

        Comment comment = new Comment(
                                      "This status is set to true in another method.");
        comment.setInline(Boolean.TRUE);
        okTestResultSkip.addComment(comment);

    }

    @Test
    public void testOkTestResult() {
        Assert.assertNotNull(okTestResult);

        Assert.assertTrue(okTestResult.getTestNumber() > 0);

        Assert.assertEquals(okTestResult.getStatus(), StatusValues.OK);

        Assert.assertNull(okTestResult.getDirective());
    }

    @Test
    public void testNotOkTestResult() {
        Assert.assertNotNull(notOkTestResult);

        Assert.assertTrue(notOkTestResult.getTestNumber() > 0);

        Assert.assertEquals(notOkTestResult.getStatus(), StatusValues.NOT_OK);

        Assert.assertNull(notOkTestResult.getDirective());
    }

    @Test
    public void testOkTestResultSkip() {
        Assert.assertNotNull(okTestResultSkip);

        Assert.assertTrue(okTestResultSkip.getTestNumber() > 0);

        okTestResultSkip.setStatus(StatusValues.OK);

        Assert.assertEquals(okTestResultSkip.getStatus(), StatusValues.OK);

        Assert.assertNotNull(okTestResultSkip.getDirective());
    }

    @Test
    public void testInlineComment() {
        Assert.assertTrue(okTestResultSkip.getComments().get(0).isInline());
    }

    @Test
    public void testCommentText() {
        Assert.assertEquals(okTestResultSkip.getComments().get(0).getText(),
                            "This status is set to true in another method.");
    }

}
