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
 * Represents a Bail Out TAP Line. The test execution should be suspended if
 * there is a Bail Out.
 *
 * @since 1.0
 */
public class BailOut extends TapResult {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -354950715195034445L;

    /**
     * Reason to Bail Out.
     */
    private final String reason;

    /**
     * Constructor with parameter.
     *
     * @param reason Reason to Bail Out.
     */
    public BailOut(String reason) {
        super();
        this.reason = reason;
    }

    /**
     * Get the reason for bailing out.
     *
     * @return Reason to Bail Out.
     */
    public String getReason() {
        return this.reason;
    }
}
