/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
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
import java.io.Writer;

import org.tap4j.model.TestSet;
import org.tap4j.representer.Representer;

/**
 * Produces a TAP Stream.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public interface Producer {

    /**
     * Returns a String representing the TAP Stream produced from a TestSet.
     * 
     * @param testSet TestSet
     * @return TAP Stream
     * @throws ProducerException
     */
    String dump(TestSet testSet);

    /**
     * Writes the TAP Stream produced from a TestSet into a Writer.
     * 
     * @param testSet TestSet
     * @param writer Writer
     * @throws ProducerException
     */
    void dump(TestSet testSet, Writer writer);

    /**
     * Writes the TAP Stream into an output File.
     * 
     * @param testSet TestSet
     * @param output Output File
     * @throws ProducerException
     */
    void dump(TestSet testSet, File output);

    /**
     * Returns the Representer used in the Producer.
     * 
     * @return Representer
     */
    Representer getRepresenter();

}
