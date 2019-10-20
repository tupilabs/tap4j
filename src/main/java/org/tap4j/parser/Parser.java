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
package org.tap4j.parser;

import java.io.File;
import java.io.InputStreamReader; // referenced in JavaDoc

import org.tap4j.model.TestSet;

/**
 * TAP regex parser.
 *
 * @since 1.0
 */
public interface Parser {

    /**
     * Parses a TAP Stream.
     *
     * @param tapStream TAP Stream
     * @return Test Set
     */
    TestSet parseTapStream(Readable tapStream);

    /**
     * Parses a TAP Stream.
     *
     * @param tapStream TAP Stream
     * @return Test Set
     */
    TestSet parseTapStream(String tapStream);

    /**
     * Parses a TAP File.
     *
     * Note: this method must only be used if this Parser was created with
     * a specified character set encoding <em>and</em> {@code tapFile} is
     * known to be written in that same encoding. Otherwise, the caller (which
     * probably knows best the file's encoding) can wrap the file in an
     * {@link InputStreamReader} and pass that to
     * {@link #parseTapStream(Readable) parseTapStream(Readable)}.
     *
     * @param tapFile TAP File
     * @return Test Set
     */
    TestSet parseFile(File tapFile);

}
