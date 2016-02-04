package org.tap4j.model;

import java.util.regex.Pattern;

/**
 * Patterns.
 */
final public class Patterns {

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
    static final String REGEX_TEXT = "((\\s|\\t)*)(.*)";
    
    /**
     * TAP Header Regex.
     */
    static final String REGEX_HEADER = "((\\s|\\t)*)?TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";

    /**
     * TAP Plan Regex.
     */
    static final String REGEX_PLAN = "((\\s|\\t)*)?(\\d+)(\\.{2})(\\d+)\\s*(#\\s*(SKIP|skip)\\s*([^#]+))?\\s*(#\\s*(.*))?";

    /**
     * TAP Test Result Regex.
     */
    static final String REGEX_TEST_RESULT = "((\\s|\\t)*)?(ok|not ok)\\s*(\\d*)\\s*([^#]*)?\\s*"
            + "(#\\s*(SKIP|skip|TODO|todo)\\s*([^#]*))?\\s*(#\\s*(.*))?";

    /**
     * TAP Bail Out! Regex.
     */
    static final String REGEX_BAIL_OUT = "((\\s|\\t)*)?Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";

    /**
     * TAP Comment Regex.
     */
    static final String REGEX_COMMENT = "((\\s|\\t)*)?#\\s*(.*)";

    /**
     * TAP Footer Regex.
     */
    static final String REGEX_FOOTER = "((\\s|\\t)*)?TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";

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