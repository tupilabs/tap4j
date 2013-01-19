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
package org.tap4j.util;

/**
 * Test Directive value. Valid values are SKIP and TOD O, represent respectively
 * "SKIP" and "TOD O". (Extra space added purposefully to avoid task scanning
 * problems).
 *
 * @since 1.0
 */
public enum DirectiveValues {

    /**
     * Valid values.
     */
    SKIP("SKIP"), TODO("TODO");

    /**
     * The text of the directive.
     */
    private String textValue;

    /**
     * Constructor with parameter.
     *
     * @param textValue Directive text value.
     */
    DirectiveValues(String textValue) {
        this.textValue = textValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.textValue;
    }

    /**
     * Get a directive value for a given string.
     * @param textValue String
     * @return Directive value
     */
    public static DirectiveValues get(String textValue) {
        if ("skip".equalsIgnoreCase(textValue)) {
            return DirectiveValues.SKIP;
        } else if ("todo".equalsIgnoreCase(textValue)) {
            return DirectiveValues.TODO;
        }
        return null;
    }

}
