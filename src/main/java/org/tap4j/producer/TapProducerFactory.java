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
package org.tap4j.producer;

import org.tap4j.representer.DumperOptions;
import org.tap4j.representer.Tap13Representer;
import org.tap4j.representer.TapJunitRepresenter;


/**
 * Factory class to produce TAP Producers.
 *
 * @since 3.1
 */
public final class TapProducerFactory {

    /**
     * Private constructor.
     */
    private TapProducerFactory() {
        throw new AssertionError("Private constructor called!");
    }

    /**
     * Create a TAP 13 producer.
     *
     * @return TapProducer
     */
    public static TapProducer makeTap13Producer() {
        return new TapProducer();
    }

    /**
     * Create a TAP 13 producer with YAMLish.
     *
     * @return TapProducer
     */
    public static TapProducer makeTap13YamlProducer() {
        DumperOptions options = new DumperOptions();
        options.setPrintDiagnostics(true);
        return new TapProducer(new Tap13Representer(options));
    }

    /**
     * Create a TAP 13 producer with YAMLish.
     *
     * @param options the {@link DumperOptions}
     * @return TapProducer
     */
    public static TapProducer makeTap13YamlProducer(DumperOptions options) {
        return new TapProducer(new Tap13Representer(options));
    }
    
    /**
     * Create a TAP JUnit producer.
     *
     * @param name JUnit file name
     * @return TapProducer
     */
    public static TapProducer makeTapJunitProducer(String name) {
        return new TapProducer(new TapJunitRepresenter(name));
    }
}
