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

import org.tap4j.parser.Tap13Parser;

/**
 * Factory class to produce TAP Consumers.
 *
 * @since 1.0
 */
public final class TapConsumerFactory {

    /**
     * Default constructor.
     */
    private TapConsumerFactory() {
        super();
    }

    /**
     * Produces a new TAP version 13 Consumer.
     *
     * @return TAP Consumer.
     */
    public static TapConsumer makeTap13Consumer() {
        return new TapConsumerImpl();
    }

    /**
     * Produces a new TAP version 13 Consumer with YAML diagnostics.
     *
     * @return TAP Consumer with YAML support.
     */
    public static TapConsumer makeTap13YamlConsumer() {
        return new TapConsumerImpl(new Tap13Parser(true));
    }

    /**
     * Produces a new TAP version 13 Consumer with YAML diagnostics.
     *
     * @return TAP Consumer with YAML support.
     */
    public static TapConsumer makeTap13YamlConsumerWithoutSubtests() {
        return new TapConsumerImpl(new Tap13Parser(false));
    }

}
