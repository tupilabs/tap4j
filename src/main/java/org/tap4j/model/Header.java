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
package org.tap4j.model;

/**
 * TAP Header. Each TAP File has at most one header in the beginning of it. The
 * only thing that can precede the Header are comments. However a Header is not
 * required in a TAP File (i.e. the Header is optional).
 *
 * @since 1.0
 */
public class Header extends TapElement {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -3823455733333632836L;

    /**
     * TAP file version.
     */
    private final Integer version;

    /**
     * Constructor with parameter.
     *
     * @param version TAP file version.
     */
    public Header(Integer version) {
        super();
        this.version = version;
    }

    /**
     * @return The TAP file version.
     */
    public Integer getVersion() {
        return this.version;
    }
}
