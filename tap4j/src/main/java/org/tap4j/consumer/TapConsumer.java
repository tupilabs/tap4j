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
package org.tap4j.consumer;

import java.io.File;
import java.io.InputStreamReader; // referenced in JavaDoc

import org.tap4j.model.TestSet;
import org.tap4j.parser.Parser;

/**
 * TAP Consumer is the responsible for generating the TAP Stream.
 *
 * @since 1.0
 */
public interface TapConsumer {

    /**
     * Parses a TAP File.
     *
     * Note: this method only works if the {@code TapConsumer} was constructed
     * with an assumed character set encoding, <em>and</em> {@code file} really
     * is encoded in that assumed encoding.
     *
     * For a more flexible approach, the caller (who should know best about the
     * encoding of any given input file) should have already
     * wrapped the file in an {@link InputStreamReader}, and should pass that to
     * {@link #load(Readable)}.
     *
     * @param file TAP File.
     * @return TestSet
     * @throws TapConsumerException
     */
    TestSet load(File file);

    /**
     * Parses a TAP Stream.
     *
     * @param tapStream TAP Stream
     * @return TestSet
     * @throws TapConsumerException
     */
    TestSet load(Readable tapStream);

    /**
     * Parses a TAP Stream.
     *
     * @param tapStream TAP Stream
     * @return TestSet
     * @throws TapConsumerException
     */
    TestSet load(String tapStream);

    /**
     * Returns the TestSet resulted from parsing a TAP File or TAP Stream.
     *
     * @return TestSet
     */
    TestSet getTestSet();

    /**
     * Returns the Parser used in the Consumer.
     *
     * @return Parser
     */
    Parser getParser();

}
