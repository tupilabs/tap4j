/*
 * The MIT License
 *
 * Copyright (c) 2010-2015 tap4j team (see AUTHORS)
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
 */package org.tap4j.parser.issueGitHub33;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

import java.io.File;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/*
*
* If the diagnostics string that is produced contains more tap output with YAML in is diagnostics tap4j bails out, incorrectly deducing that the YAML in the diagnostics (which is at a different indentation level and should be ignored by the parser and just included as part of the diagnostic text) should be parsed and it falls over.


The following tap stream demonstrates this issue, with the diagnostics actually containing a tap stream that is not part of the "main" tap stream.


TAP version 13
1..1
ok 1 - sometest
  ---
    datetime: 2013-11-14T15:42:54
    raw_output: |
      Running sometest
      .....
      ================================================================================
      Verification failed.

          ---------------------
          Framework Diagnostic:
          ---------------------
          ContainsSubstring failed.
          --> The string must contain the substring.

          Actual String:
              TAP version 13
              1..2
                ---
                  datetime: 2013-09-12T08:35:14
                ...
              ok 1 - testcases.SingleSilentTest
                ---
                  datetime: 2013-09-12T08:35:15
                  raw_output:
                ...
              not ok 2 - testcases.SimpleTestWithSharedFixture
                ---
                  datetime: 2013-09-12T08:35:16
                  raw_output:
                ...

          Expected Substring:

              ok 2 - testcases.SimpleTestWithSharedFixture
                ---
                  datetime: 2013-09-12T08:35:16
                ...

      ================================================================================
      Done sometest
      __________
  ...*/

/**
 * tap4j trips over YAML/TAP output that is included in the diagnostics of its own YAML
 * @since 4.0.9
 */
public class TestYamlWithYamlInDiagnostics {

    @Test
    public void testYamlWithYamlInDiagnostics() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
            .getResource("/org/tap4j/parser/issueGitHub33/issue-33_tap_stream.tap")
            .getFile()));

        assertEquals(1, testSet.getTestResults().size());

        TestResult tr1 = testSet.getTestResult(1);
        Map<String, Object> yaml = tr1.getDiagnostic();
        assertNotNull(yaml);

        String multiLineYaml = (String) yaml.get("raw_output");

        assertThat(multiLineYaml, is(equalTo(
                                                     "Running sometest\n" +
                                                     ".....\n" +
                                                     "================================================================================\n" +
                                                     "Verification failed.\n" +
                                                     "\n" +
                                                     "    ---------------------\n" +
                                                     "    Framework Diagnostic:\n" +
                                                     "    ---------------------\n" +
                                                     "    ContainsSubstring failed.\n" +
                                                     "    --> The string must contain the substring.\n" +
                                                     "\n" +
                                                     "    Actual String:\n" +
                                                     "        TAP version 13\n" +
                                                     "        1..2\n" +
                                                     "          ---\n" +
                                                     "            datetime: 2013-09-12T08:35:14\n" +
                                                     "          ...\n" +
                                                     "        ok 1 - testcases.SingleSilentTest\n" +
                                                     "          ---\n" +
                                                     "            datetime: 2013-09-12T08:35:15\n" +
                                                     "            raw_output:\n" +
                                                     "          ...\n" +
                                                     "        not ok 2 - testcases.SimpleTestWithSharedFixture\n" +
                                                     "          ---\n" +
                                                     "            datetime: 2013-09-12T08:35:16\n" +
                                                     "            raw_output:\n" +
                                                     "          ...\n" +
                                                     "\n" +
                                                     "    Expected Substring:\n" +
                                                     "\n" +
                                                     "        ok 2 - testcases.SimpleTestWithSharedFixture\n" +
                                                     "          ---\n" +
                                                     "            datetime: 2013-09-12T08:35:16\n" +
                                                     "          ...\n" +
                                                     "\n" +
                                                     "================================================================================\n" +
                                                     "Done sometest\n" +
                                                     "__________\n")));
    }

}
