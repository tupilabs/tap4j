package org.tap4j.parser;

import java.util.regex.Pattern;

/**
 * Constants.
 */
final class Constants {

    /**
     * Hidden constructor, since this class is not supposed to be used.
     */
    private Constants() { // hidden as this is a final utility class
        super();
    }

    /* -- Regular expressions -- */

    /**
     * Pattern used for finding the indentation space in new lines.
     */
    static final Pattern INDENTATION_PATTERN = Pattern
            .compile("((\\s|\\t)*)?.*");

    /**
     * TAP Header Regex.
     */
    static final String REGEX_HEADER = "\\s*TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";

    /**
     * TAP Plan Regex.
     */
    static final String REGEX_PLAN = "\\s*(\\d+)(\\.{2})(\\d+)\\s*(#\\s*(SKIP|skip)\\s*([^#]+))?\\s*(#\\s*(.*))?";

    /**
     * TAP Test Result Regex.
     */
    static final String REGEX_TEST_RESULT = "\\s*(ok|not ok)\\s*(\\d*)\\s*([^#]*)?\\s*"
            + "(#\\s*(SKIP|skip|TODO|todo)\\s*([^#]*))?\\s*(#\\s*(.*))?";

    /**
     * TAP Bail Out! Regex.
     */
    static final String REGEX_BAIL_OUT = "\\s*Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";

    /**
     * TAP Comment Regex.
     */
    static final String REGEX_COMMENT = "\\s*#\\s*(.*)";

    /**
     * TAP Footer Regex.
     */
    static final String REGEX_FOOTER = "\\s*TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";

    /* -- Patterns -- */

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
