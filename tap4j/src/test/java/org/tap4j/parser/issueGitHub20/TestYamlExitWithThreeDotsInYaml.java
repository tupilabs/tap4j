package org.tap4j.parser.issueGitHub20;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import static org.hamcrest.core.StringContains.containsString;

/*Somewhere near line 250 of Tap13Parser we see the following code:

if (tapLine.trim().equals("---")) {
    state.setCurrentlyInYaml(true);
    return;
} else if (tapLine.trim().equals("...")) {
    state.setCurrentlyInYaml(false);
    return;
}
// etc
However, it seems we shouldn't be trimming the the "..." to close the yaml block. Here is an example of a tap stream
where this assumption is not valid:

TAP version 13
1..1
ok 1 - sometest
  ---
    datetime: 2013-11-14T15:42:54
    raw_output: |
      Running sometest
      ..........
      ..........
      ...
      Done sometest
      __________
  ...
Note that using pipe the "|" character to produce formatted text is fully supported by YAML, YAMLish, and SankeYAML.
However, as you can see from the stream above tap4j trips on the "..." which is included in the raw_output field instead
of the real one that closes the yaml block. Really, when we setCurrently in YAML we need to record the indentation at
that time as the "YAML indentation" and probably store that in the memento. Then, instead of trimming the "..." we need
to see if the line is exactly "..." with the right indentation to exit out of the yaml block. Actually, I think that the
"..." is optional in YAMLish and the indentation is the most important piece of information to determine whether the
yaml block is finished yet. Perhaps we should look for the change in indentation rather than the "..." and treat the
"..." as a readability enhancer that is largely ignored.

Also, btw, it seems that the Jenkins TAP plugin does not respect the newlines that are present in the preformatted text
(raw_output above) and it seems to assume the value can always be displayed in a single line. However, that is another
issue.*/
/**
 * Parser has incorrect logic for ending yaml diagnostics
 * @since 4.0.6
 */
public class TestYamlExitWithThreeDotsInYaml {

    @Test
    public void testYamlExitWithThreeDotsInYaml() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(TestDirectives.class
            .getResource("/org/tap4j/parser/issueGitHub20/issue-20-tap-stream.tap")
            .getFile()));

        assertEquals(1, testSet.getTestResults().size());
        
        TestResult tr1 = testSet.getTestResult(1);
        Map<String, Object> yaml = tr1.getDiagnostic();
        assertNotNull(yaml);

        String multiLineYaml = (String) yaml.get("raw_output");

        assertThat(multiLineYaml, containsString("Done sometest"));
    }
    
}