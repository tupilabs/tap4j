package org.tap4j.parser.issueGitHub16;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

/**
 * Optional test numbers are not working
 *
 * @since 4.0.4
 */
public class TestOptionalTestNumbers {

    @Test
    public void testOptionalTestNumbers() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
            .getResource("/org/tap4j/parser/issueGitHub16/issue-16-tap-stream.tap")
            .getFile()));

        assertEquals(3, testSet.getTestResults().size());
    }

}
