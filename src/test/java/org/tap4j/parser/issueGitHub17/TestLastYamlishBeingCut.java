/*
 * The MIT License
 *
 * Copyright (c) 2010-2026 tap4j team (see AUTHORS)
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

package org.tap4j.parser.issueGitHub17;


import org.junit.jupiter.api.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
When parsing the TAP Stream below with a TAPConsumerFactory.makeTap13YamlConsumer,
it seems that tap4j (using 4.0.4) does not hold onto the YAML diagnostic for anotherDummyTest.

Note that it finds it correctly when there are no subtests, and the subtests themselves are
handled correctly in the presence of the YAML, but it is not placing this value into the Map
when there are subtests.

TAP version 13
1..2
  ---
    datetime: 20100101T000000
  ...
ok 1 - someDummyTest
  ---
    datetime: 20100101T000002
  ...
  1..5
  ok
  ok
  ok
  ok
  ok
ok 2 - anotherDummyTest
  ---
    datetime: 20100101T000005
  ...
  1..1
  ok*/

/**
 * Stream with subtests chopping off the last YAML diagnostic.
 *
 * @since 4.0.5
 */
public class TestLastYamlishBeingCut {

    @Test
    public void testLastYamlishBeingCut() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(Objects.requireNonNull(TestDirectives.class
                .getResource("/org/tap4j/parser/issueGitHub17/issue-17-tap-stream.tap"))
            .getFile()));

        assertEquals(2, testSet.getTestResults().size());

        TestResult tr1 = testSet.getTestResult(1);
        TestSet tr1Subtest = tr1.getSubtest();
        assertNotNull(tr1Subtest);
        assertEquals(5, tr1Subtest.getTestResults().size());

        TestResult tr2 = testSet.getTestResult(2);
        TestSet tr2Subtest = tr2.getSubtest();
        assertNotNull(tr2Subtest);
        assertEquals(1, tr2Subtest.getTestResults().size());

        assertEquals(1, tr2.getDiagnostic().size());

        TestResult tr2_1 = tr2Subtest.getTestResult(1);
        assertNotNull(tr2_1);
    }

}
