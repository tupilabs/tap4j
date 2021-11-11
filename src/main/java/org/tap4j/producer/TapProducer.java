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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.tap4j.model.TestSet;
import org.tap4j.representer.Representer;
import org.tap4j.representer.RepresenterException;
import org.tap4j.representer.Tap13Representer;

/**
 * TAP Producer is responsible for writing the TAP Stream onto some media.
 *
 * @since 1.0
 */
public class TapProducer implements Producer {

    /**
     * Represents the TAP Stream.
     */
    private final Representer representer;

    /**
     * Default constructor.
     */
    public TapProducer() {
        super();
        representer = new Tap13Representer();
    }

    /**
     * Constructor with parameter.
     *
     * @param representer Representer
     */
    public TapProducer(Representer representer) {
        this.representer = representer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dump(TestSet testSet) {
        try {
            return this.representer.representData(testSet);
        } catch (RepresenterException re) {
            throw new ProducerException(String.format("Failed to produce test set dump: %s", re.getMessage()), re);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dump(TestSet testSet, Writer writer) {
        String tapStream;
        try {
            tapStream = this.dump(testSet);
            writer.append(tapStream);
        } catch (RepresenterException re) {
            throw new ProducerException(String.format("Failed to dump Test Set to writer: %s", re.getMessage()), re);
        } catch (IOException e) {
            throw new ProducerException(String.format("Failed to dump TAP Stream: %s", e.getMessage()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dump(TestSet testSet, File output) {
        Charset charset;
        if (representer instanceof Tap13Representer) {
            charset = Charset.forName(((Tap13Representer) representer).getOptions().getCharset());
        } else {
            charset = Charset.defaultCharset();
        }

        try (FileOutputStream outputStream = new FileOutputStream(output); OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset.newEncoder())) {
            this.dump(testSet, writer);
        } catch (IOException e) {
            throw new ProducerException(String.format("Failed to dump TAP Stream: %s", e.getMessage()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Representer getRepresenter() {
        return this.representer;
    }

}
