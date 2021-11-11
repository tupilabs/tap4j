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
 * An ordinary text. It can be added to the TestSet, however it isn't parsed by
 * the TAP Consumer. Used only for debugging, customization or back
 * compatibility.
 *
 * @since 1.0
 */
public class Text extends TapResult {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5408008204269040841L;

    /**
     * Value of Text.
     */
    private final String value;

    /**
     * Indentation string.
     */
    protected String indentationString;

    /**
     * Constructor with parameter.
     *
     * @param value String value.
     */
    public Text(String value) {
        super();
        this.value = value;
    }

    /**
     * @return Value of the String.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @return the indentation string
     */
    public String getIndentationString() {
        return indentationString;
    }

    /**
     * @param indentationString the new indentation string
     */
    public void setIndentationString(String indentationString) {
        this.indentationString = indentationString;
    }

}
