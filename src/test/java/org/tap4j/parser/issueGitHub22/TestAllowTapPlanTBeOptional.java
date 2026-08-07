package org.tap4j.parser.issueGitHub22;

import org.junit.jupiter.api.Test;
import org.tap4j.model.TestSet;
import org.tap4j.parser.Tap13Parser;
import org.tap4j.parser.issue3406964.TestDirectives;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parser has incorrect logic for ending YAML diagnostics.
 * @since 4.0.7
 */
public class TestAllowTapPlanTBeOptional {

    @Test
    public void testAllowTapPlanTBeOptional() {
        Tap13Parser parser = new Tap13Parser(Charset.defaultCharset().toString(), true, false);
        TestSet testSet = parser.parseFile(new File(Objects.requireNonNull(TestDirectives.class
                .getResource("/org/tap4j/parser/issueGitHub22/issue-22-tap-stream.tap"))
            .getFile()));

        assertEquals(4, testSet.getTestResults().size());
    }

}
