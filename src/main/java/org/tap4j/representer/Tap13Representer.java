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
package org.tap4j.representer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.TapElement;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

/**
 * A TAP 13 representer.
 *
 * @since 1.0
 */
public class Tap13Representer implements Representer {

    /**
     * Line separator.
     */
    private static final CharSequence LINE_SEPARATOR = "\n";

    /**
     * Dumper options.
     */
    private final org.tap4j.representer.DumperOptions options;

    /**
     * YAML parser and emitter.
     */
    private Yaml yaml = null;

    /**
     * Default constructor.
     */
    public Tap13Representer() {
        this(new org.tap4j.representer.DumperOptions());
    }

    /**
     * @param options Dumper options
     */
    public Tap13Representer(org.tap4j.representer.DumperOptions options) {
        super();
        this.options = options;
        if (options.isPrintDiagnostics()) {
            final DumperOptions yamlDumperOptions = new DumperOptions();
            yamlDumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            yamlDumperOptions.setLineBreak(LineBreak.getPlatformLineBreak());
            yamlDumperOptions.setExplicitStart(true);
            yamlDumperOptions.setExplicitEnd(true);
            yaml = new Yaml(yamlDumperOptions);
        }
    }

    /**
     * @return the options
     */
    public org.tap4j.representer.DumperOptions getOptions() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    public String representData(TestSet testSet) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printHeader(pw, testSet.getHeader());
        printPlan(pw, testSet.getPlan());
        for (TapElement tapLine : testSet.getTapLines()) {
            printTapLine(pw, tapLine);
        }
        printFooter(pw, testSet.getFooter());
        return sw.toString();
    }

    /**
     * @param pw Print Writer
     * @param tapResult TAP test result
     */
    protected void printTapLine(PrintWriter pw, TapElement tapResult) {
        if (tapResult instanceof BailOut) {
            printBailOut(pw, (BailOut) tapResult);
        } else if (tapResult instanceof Comment) {
            printComment(pw, (Comment) tapResult);
            pw.append(LINE_SEPARATOR);
        } else if (tapResult instanceof TestResult) {
            printTestResult(pw, (TestResult) tapResult);
        }
    }

    /**
     * @param pw Print Writer
     * @param testResult TAP test result
     */
    protected void printTestResult(PrintWriter pw, TestResult testResult) {
        if (testResult.getSubtest() != null) {
            int indent = this.options.getIndent();
            int spaces = this.options.getSpaces();
            this.options.setIndent(indent + spaces);
            pw.append(this.representData(testResult.getSubtest()));
            this.options.setIndent(indent);
        }
        printFiller(pw);
        pw.append(testResult.getStatus().toString());
        pw.append(String.valueOf(' ')).append(Integer.toString(testResult.getTestNumber()));
        if (testResult.getDescription() != null && testResult.getDescription().trim().length() > 0) {
            pw.append(String.valueOf(' ')).append(testResult.getDescription());
        }
        if (testResult.getDirective() != null) {
            pw.append(" # ").append(testResult.getDirective().getDirectiveValue().toString());
            String reason = testResult.getDirective().getReason();
            if (reason != null && reason.trim().length() > 0) {
                pw.append(String.valueOf(' ')).append(testResult.getDirective().getReason());
            }
        }
        List<Comment> comments = testResult.getComments();
        if (comments.size() > 0) {
            for (Comment comment : comments) {
                if (comment.isInline()) {
                    pw.append(' ');
                } else {
                    pw.append(LINE_SEPARATOR);
                }
                printComment(pw, comment);
            }
        }
        printDiagnostic(pw, testResult);
        pw.append(LINE_SEPARATOR);
    }

    /**
     * @param pw Print Writer
     * @param bailOut Bail Out!
     */
    protected void printBailOut(PrintWriter pw, BailOut bailOut) {
        printFiller(pw);
        pw.append("Bail out!");
        if (bailOut.getReason() != null) {
            pw.append(String.valueOf(' ')).append(bailOut.getReason());
        }
        if (bailOut.getComment() != null) {
            pw.append(' ');
            printComment(pw, bailOut.getComment());
        }
        printDiagnostic(pw, bailOut);
        pw.append(LINE_SEPARATOR);
    }

    /**
     * @param pw Print Writer
     * @param footer Footer
     */
    protected void printFooter(PrintWriter pw, Footer footer) {
        if (footer != null) {
            printFiller(pw);
            pw.append("TAP ").append(footer.getText());
            if (footer.getComment() != null) {
                pw.append(' ');
                printComment(pw, footer.getComment());
            }
            printDiagnostic(pw, footer);
            pw.append(LINE_SEPARATOR);
        }
    }

    /**
     * @param pw Print Writer
     * @param plan Plan
     */
    protected void printPlan(PrintWriter pw, Plan plan) {
        if (plan != null) {
            printFiller(pw);
            pw.append(String.valueOf(plan.getInitialTestNumber())).append("..").append(String.valueOf(plan.getLastTestNumber()));
            if (plan.getSkip() != null) {
                pw.append(" skip ");
                pw.append(plan.getSkip().getReason());
            }
            if (plan.getComment() != null) {
                pw.append(' ');
                this.printComment(pw, plan.getComment());
            }
            printDiagnostic(pw, plan);
            pw.append(LINE_SEPARATOR);
        } else {
            if (options.isAllowEmptyTestPlan() == Boolean.FALSE) {
                throw new RepresenterException("Missing required TAP Plan");
            }
        }
    }

    /**
     * @param pw Print Writer
     * @param header Header
     */
    protected void printHeader(PrintWriter pw, Header header) {
        if (header != null) {
            printFiller(pw);
            pw.append("TAP version ").append(String.valueOf(header.getVersion()));
            if (header.getComment() != null) {
                pw.append(' ');
                this.printComment(pw, header.getComment());
            }
            printDiagnostic(pw, header);
            pw.append(LINE_SEPARATOR);
        }
    }

    /**
     * @param pw Print Writer
     * @param comment Comment
     */
    protected void printComment(PrintWriter pw, Comment comment) {
        pw.append("# ").append(comment.getText());
    }

    /**
     * Prints diagnostic of the TAP Element into the Print Writer.
     *
     * @param pw Print Writer
     * @param tapElement TAP element
     */
    protected void printDiagnostic(PrintWriter pw, TapElement tapElement) {
        if (this.yaml != null) {
            Map<String, Object> diagnostic = tapElement.getDiagnostic();
            if (diagnostic != null && !diagnostic.isEmpty()) {
                String diagnosticText = yaml.dump(diagnostic);
                diagnosticText = diagnosticText.replaceAll("((?m)^)", "  ");
                pw.append(LINE_SEPARATOR);
                printFiller(pw);
                pw.append(diagnosticText);
            }
        }
    }

    /**
     * Print filler.
     *
     * @param pw Print Writer
     */
    protected void printFiller(PrintWriter pw) {
        if (this.options.getIndent() > 0) {
            for (int i = 0; i < options.getIndent(); i++) {
                pw.append(' ');
            }
        }
    }

}
