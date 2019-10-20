/*
 * The MIT License
 *
 * Copyright (c) 2016 tap4j team (see AUTHORS)
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

import java.util.regex.Pattern;

/**
 * Utility class that holds regular expression patterns for the TAP parsers.
 */
public final class Patterns {

    /**
     * Hidden constructor, since this class is not supposed to be used.
     */
    private Patterns() { // hidden as this is a final utility class
        super();
    }

    /* -- Regular expressions -- */

    /**
     * TAP Text Regex.
     */
    static final String REGEX_TEXT = "(\\s*)(.*)";

    /**
     * TAP Header Regex.
     */
    static final String REGEX_HEADER = "(\\s*)TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";

    /**
     * TAP Plan Regex.
     */
    static final String REGEX_PLAN = "(\\s*)(\\d+)(\\.{2})(\\d+)"
            + "\\s*(#\\s*(SKIP|skip)\\s*([^#]+))?\\s*(#\\s*(.*))?";

    /**
     * TAP Test Result Regex.
     */
    static final String REGEX_TEST_RESULT = "(\\s*)(ok|not ok)\\s*(\\d*)\\s*([^#]*)?\\s*"
            + "(#\\s*(SKIP|skip|TODO|todo)\\s*([^#]*))?\\s*(#\\s*(.*))?";

    /**
     * TAP Bail Out! Regex.
     */
    static final String REGEX_BAIL_OUT = "(\\s*)Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";

    /**
     * TAP Comment Regex.
     */
    static final String REGEX_COMMENT = "(\\s*)#\\s*(.*)";

    /**
     * TAP Footer Regex.
     */
    static final String REGEX_FOOTER = "(\\s*)TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";

    /* -- Patterns -- */

    /**
     * TAP Text Regex Pattern.
     */
    static final Pattern TEXT_PATTERN = Pattern.compile(REGEX_TEXT);

    /**
     * TAP Header Regex Pattern.
     */
    static final Pattern HEADER_PATTERN = Pattern.compile(REGEX_HEADER);

    /**
     * TAP Plan Regex Pattern.
     */
    static final Pattern PLAN_PATTERN = Pattern.compile(REGEX_PLAN);

    /**
     * TAP Test Result Regex Pattern.
     */
    static final Pattern TEST_RESULT_PATTERN = Pattern
            .compile(REGEX_TEST_RESULT);

    /**
     * TAP Bail Out! Regex Pattern.
     */
    static final Pattern BAIL_OUT_PATTERN = Pattern.compile(REGEX_BAIL_OUT);

    /**
     * TAP Comment Regex Pattern.
     */
    static final Pattern COMMENT_PATTERN = Pattern.compile(REGEX_COMMENT);

    /**
     * TAP Footer Regex Pattern.
     */
    static final Pattern FOOTER_PATTERN = Pattern.compile(REGEX_FOOTER);
}
