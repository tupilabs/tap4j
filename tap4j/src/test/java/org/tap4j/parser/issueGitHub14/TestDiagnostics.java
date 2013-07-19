package org.tap4j.parser.issueGitHub14;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

/**
 * Diagnostics are added to all test cases, after one with diagnostics was found.
 * 
 * @since 4.0.3
 */
public class TestDiagnostics {

    @Test
    public void testDiagnostics() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
            .getResource("/org/tap4j/parser/issueGitHub14/issue-14-tap-stream.tap")
            .getFile()));

        for (TestResult tr : testSet.getTestResults()) {
            if (tr.getTestNumber() == 3) {
                assertTrue(tr.getDiagnostic().size() > 0);
            } else {
                assertTrue(tr.getDiagnostic().size() == 0);
            }
        }
    }

}
