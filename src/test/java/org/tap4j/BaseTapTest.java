
/*
 * The MIT License
 *
 * Copyright (c) 2010 tap4j team (see AUTHORS)
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
package org.tap4j;

import java.io.File;

import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerImpl;
import org.tap4j.model.TestSet;
import org.tap4j.parser.Parser;
import org.tap4j.parser.Tap13Parser;

/**
 * Base class for TAP tests.
 */
public class BaseTapTest {

    /**
     * Get a test set for a given file name.
     * @param name File name.
     * @return Test Set.
     */
    protected TestSet getTestSet(String name) {
        return this.getTestSet(new Tap13Parser(), name);
    }

    /**
     * Get a test set for given parser and file name.
     * @param parser Parser.
     * @param name File name.
     * @return Test Set.
     */
    protected TestSet getTestSet(Parser parser, String name) {
        TapConsumer consumer = getConsumer(parser);
        return consumer
                .load(new File(getClass()
                    .getResource(name)
                    .getFile()));
    }

    /**
     * Get a tap consumer.
     * @return TAP Consumer.
     */
    protected TapConsumer getConsumer() {
        return getConsumer(new Tap13Parser());
    }

    /**
     * Get a consumer for with a given parser.
     * @param parser TAP parser.
     * @return TAP Consumer.
     */
    protected TapConsumer getConsumer(Parser parser) {
        return new TapConsumerImpl(parser);
    }

}
