/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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

import java.util.List;

import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Comment;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for issue 3525607
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class TestIssue3525607 {

    @Test
    public void testTestResultWithCommentDiagnostics() {
        final String tap = "1..2\n" +
        		"ok 1 - OK\n" +
        		"# No errors found\n" +
        		"not ok 2\n" +
        		"# Invalid stream character\n" +
        		"# Missing end transmission signal\n" +
        		"# Aborting mission!\n" +
        		"ok";
        TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = consumer.load(tap);
        
        Assert.assertEquals(testSet.getTestResult(1).getComments().get(0).getText(), "No errors found");
        
        String expected = "Invalid stream character\n" +
        		"Missing end transmission signal\n" +
        		"Aborting mission!\n";
        
        StringBuilder actualCommentText = new StringBuilder();
        TestResult testResult = testSet.getTestResult(2);
        List<Comment> comments = testResult.getComments();
        
        for(Comment comment : comments) {
            actualCommentText.append(comment.getText());
            actualCommentText.append("\n");
        }
        
        Assert.assertEquals(actualCommentText.toString(), expected);
    }
    
}
