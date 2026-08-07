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

package org.tap4j.parser.issueGitHub16;

import org.junit.jupiter.api.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;
import org.tap4j.parser.issue3406964.TestDirectives;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Optional test numbers are not working
 *
 * @since 4.0.4
 */
public class TestOptionalTestNumbers {

    @Test
    public void testOptionalTestNumbers() {
        TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
        TestSet testSet = tapConsumer.load(new File(Objects.requireNonNull(TestDirectives.class
                .getResource("/org/tap4j/parser/issueGitHub16/issue-16-tap-stream.tap"))
            .getFile()));

        assertEquals(3, testSet.getTestResults().size());
    }

}
