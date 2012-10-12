/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.tap4j.model.TestSet;
import org.tap4j.representer.Representer;
import org.tap4j.representer.RepresenterException;
import org.tap4j.representer.Tap13Representer;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TapProducer implements Producer {

    private Representer representer;

    public TapProducer() {
        super();
        representer = new Tap13Representer();
    }

    public TapProducer(Representer representer) {
        this.representer = representer;
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.producer.TapProducer#dump(org.tap4j.model.TestSet)
     */
    public String dump(TestSet testSet) {
        String dumpData = null;

        try {
            dumpData = this.representer.representData(testSet);
        } catch (RepresenterException re) {
            throw new ProducerException("Failed to produce test set dump: " +
                                        re.getMessage(), re);
        }

        return dumpData;
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.producer.TapProducer#dump(org.tap4j.model.TestSet,
     * java.io.Writer)
     */
    public void dump(TestSet testSet, Writer writer) {
        String tapStream = null;

        try {
            tapStream = this.dump(testSet);
        } catch (RepresenterException re) {
            throw new ProducerException("Failed to dump Test Set to writer: " +
                                        re.getMessage(), re);
        }

        try {
            writer.append(tapStream);
        } catch (IOException e) {
            throw new ProducerException("Failed to dump TAP Stream: " +
                                        e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.producer.TapProducer#dump(org.tap4j.model.TestSet,
     * java.io.File)
     */
    public void dump(TestSet testSet, File output) {
        String tapStream = null;

        try {
            tapStream = this.dump(testSet);
        } catch (RepresenterException re) {
            throw new ProducerException(
                                        "Failed to dump Test Set to output file '" +
                                                output +
                                                "': " +
                                                re.getMessage(), re);
        }

        try {
            FileUtils.writeStringToFile(output, tapStream);
        } catch (IOException e) {
            throw new ProducerException("Failed to dump TAP Stream: " +
                                        e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.tap4j.producer.TapProducer#getRepresenter()
     */
    public Representer getRepresenter() {
        return this.representer;
    }

}
