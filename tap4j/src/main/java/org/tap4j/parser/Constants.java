package org.tap4j.parser;

import java.util.regex.Pattern;

public final class Constants {

    private Constants() { // hidden as this is a final utility class
        super();
    }

    /* -- Regular expressions -- */

    static final Pattern INDENTANTION_PATTERN = Pattern
            .compile("((\\s|\\t)*)?.*");

    /**
     * TAP Header Regex.
     */
    static String REGEX_HEADER = "\\s*TAP\\s*version\\s*(\\d+)\\s*(#\\s*(.*))?";

    /**
     * TAP Plan Regex.
     */
    static String REGEX_PLAN = "\\s*(\\d+)(\\.{2})(\\d+)\\s*(skip\\s*([^#]+))?\\s*(#\\s*(.*))?";

    /**
     * TAP Test Result Regex.
     */
    static String REGEX_TEST_RESULT = "\\s*(ok|not ok)\\s*(\\d+)\\s*([^#]*)?\\s*(#\\s*(SKIP|skip|TODO|todo)\\s*([^#]+))?\\s*(#\\s*(.*))?";

    /**
     * TAP Bail Out! Regex.
     */
    static String REGEX_BAIL_OUT = "\\s*Bail out!\\s*([^#]+)?\\s*(#\\s*(.*))?";

    /**
     * TAP Comment Regex.
     */
    static String REGEX_COMMENT = "\\s*#\\s*(.*)";

    /**
     * TAP Footer Regex.
     */
    static String REGEX_FOOTER = "\\s*TAP\\s*([^#]*)?\\s*(#\\s*(.*))?";

    /* -- Patterns -- */

    /**
     * TAP Header Regex Pattern.
     */
    static Pattern HEADER_PATTERN = Pattern.compile(REGEX_HEADER);

    /**
     * TAP Plan Regex Pattern.
     */
    static Pattern PLAN_PATTERN = Pattern.compile(REGEX_PLAN);

    /**
     * TAP Test Result Regex Pattern.
     */
    static Pattern TEST_RESULT_PATTERN = Pattern.compile(REGEX_TEST_RESULT);

    /**
     * TAP Bail Out! Regex Pattern.
     */
    static Pattern BAIL_OUT_PATTERN = Pattern.compile(REGEX_BAIL_OUT);

    /**
     * TAP Comment Regex Pattern.
     */
    static Pattern COMMENT_PATTERN = Pattern.compile(REGEX_COMMENT);

    /**
     * TAP Footer Regex Pattern.
     */
    static Pattern FOOTER_PATTERN = Pattern.compile(REGEX_FOOTER);
}
