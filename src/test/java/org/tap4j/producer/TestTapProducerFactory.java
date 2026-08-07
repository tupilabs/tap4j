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

import org.junit.jupiter.api.Test;
import org.tap4j.representer.Representer;
import org.tap4j.representer.Tap13Representer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test Class for TAP Producer Factories.
 *
 * @since 1.0
 */
public class TestTapProducerFactory {

    @Test
    public void testMakeTap13Producer() {
        final Producer tapProducer = new TapProducer();
        final Representer tap13Representer = tapProducer.getRepresenter();
        assertInstanceOf(Tap13Representer.class, tap13Representer);
    }

    @Test
    public void testMakeTap13YamlProducer() {
        final Producer tapProducer = new TapProducer();
        final Representer tap13YamlRepresenter = tapProducer.getRepresenter();
        assertInstanceOf(Tap13Representer.class, tap13YamlRepresenter);
    }

    @Test
    public void testTapProducerFactoryConstructor() throws IllegalArgumentException {
        TapProducer producer = new TapProducer();
        assertNotNull(producer);
        Representer representer = new Tap13Representer();
        producer = new TapProducer(representer);
        assertNotNull(producer);
    }

}
