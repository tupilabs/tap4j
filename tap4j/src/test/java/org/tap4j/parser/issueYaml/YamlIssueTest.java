/*
 * The MIT License
 *
 * Copyright (c) 2018 tap4j team (see AUTHORS)
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
package org.tap4j.parser.issueYaml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.Tap13Parser;

/**
 * TAP Streams that contain corrupted YAML should be possible to parse using an extra option.
 */
public class YamlIssueTest {

    /**
     * corrupted yaml content should not break TAP parser.
     */
    @Test
    public void testParsingCorruptedYaml() {

        Tap13Parser tapParser = new Tap13Parser("UTF-8", true, true, true);

        TestSet testSet = tapParser.parseFile(new File(YamlIssueTest.class
                .getResource("/org/tap4j/parser/issueYaml/jsdom_test_result.tap")
                .getFile()));

        assertNotNull(testSet);

        final List<TestResult> testResults = testSet.getTestResults();
        assertEquals(5823, testResults.size());
    }
}
