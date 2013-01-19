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
 * Test Status. Valid values are OK and NOT OK, represent respectively "ok" and
 * "not ok".
 *
 * @since 1.0
 */
public enum StatusValues {

    /**
     * Valid values.
     */
    OK("ok"), NOT_OK("not ok");

    /**
     * Status value.
     */
    private String textValue;

    /**
     * Constructor with parameter.
     *
     * @param textValue Status text value
     */
    StatusValues(String textValue) {
        this.textValue = textValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.textValue;
    }

    public static StatusValues get(String textValue) {
        if ("ok".equals(textValue)) {
            return StatusValues.OK;
        } if ("not ok".equals(textValue)) {
            return StatusValues.NOT_OK;
        }
        return null;
    }

}
