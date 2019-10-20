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

import org.tap4j.model.TestSet;
import org.tap4j.parser.Parser;
import org.tap4j.parser.ParserException;
import org.tap4j.parser.Tap13Parser;

/**
 * Implementation of a TAP Consumer. It can use different TAP parsers. The
 * parser can change the behaviour of the consumer, as the latter delegates
 * the parsing and TAP model loading to the former.
 *
 * @since 1.0
 */
public class TapConsumerImpl implements TapConsumer {

    /**
     * TAP parser.
     */
    private final Parser parser;

    /**
     * TAP Test Set.
     */
    private TestSet testSet;

    /**
     * Default constructor.
     */
    public TapConsumerImpl() {
        parser = new Tap13Parser();
        testSet = new TestSet();
    }

    /**
     * Constructor with a parser.
     * @param parser a parser
     */
    public TapConsumerImpl(Parser parser) {
        this.parser = parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestSet getTestSet() {
        return this.testSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestSet load(File file) {
        try {
            this.testSet = this.parser.parseFile(file);
        } catch (ParserException e) {
            throw new TapConsumerException("Failed to parse file " + file
                                            + ": " + e.getMessage(), e);
        }
        return this.testSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestSet load(Readable tapStream) {
        try {
            this.testSet = this.parser.parseTapStream(tapStream);
        } catch (ParserException e) {
            throw new TapConsumerException("Failed to parse TAP Stream "
                                            + tapStream + ": " + e.getMessage(), e);
        }
        return this.testSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestSet load(String tapStream) {
        try {
            this.testSet = this.parser.parseTapStream(tapStream);
        } catch (ParserException e) {
            throw new TapConsumerException("Failed to parse TAP Stream "
                                            + tapStream + ": " + e.getMessage(), e);
        }
        return this.testSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parser getParser() {
        return this.parser;
    }

}
