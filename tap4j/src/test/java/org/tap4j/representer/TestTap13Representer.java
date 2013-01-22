package org.tap4j.representer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tap4j.model.BailOut;
import org.tap4j.model.Comment;
import org.tap4j.model.Directive;
import org.tap4j.model.Footer;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.SkipPlan;
import org.tap4j.model.TapResult;
import org.tap4j.model.TestResult;
import org.tap4j.model.Text;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

public class TestTap13Representer {

    @Test
    public void testOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(100);
        Tap13Representer repr = new Tap13Representer(options);
        assertNotNull(repr.getOptions());
        assertEquals(options, repr.getOptions());
        assertEquals(100, options.getIndent());
    }
    
    @Test
    public void testPrintTapLines() {
        Tap13Representer repr = new Tap13Representer();
        TapResult testResult = new TestResult(StatusValues.OK, 1);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, testResult);
        assertEquals("ok 1\n", sw.toString());
        
        TapResult text = new Text("yo");
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        repr.printTapLine(pw, text);
        assertEquals("", sw.toString());
    }
    
    @Test
    public void testPrintTestResultWithDirective() {
        Tap13Representer repr = new Tap13Representer();
        TestResult testResult = new TestResult(StatusValues.OK, 1);
        testResult.setDirective(new Directive(DirectiveValues.SKIP, "skip me"));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, testResult);
        assertEquals("ok 1 # SKIP skip me\n", sw.toString());
    }
    
    @Test
    public void testPrintTestResultWithDirectiveWithoutReason() {
        Tap13Representer repr = new Tap13Representer();
        TestResult testResult = new TestResult(StatusValues.OK, 1);
        testResult.setDirective(new Directive(DirectiveValues.SKIP, null));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, testResult);
        assertEquals("ok 1 # SKIP\n", sw.toString());
    }
    
    @Test
    public void testPrintTestResultWithComment() {
        Tap13Representer repr = new Tap13Representer();
        TestResult testResult = new TestResult(StatusValues.OK, 1);
        testResult.addComment(new Comment("a comment", true));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, testResult);
        assertEquals("ok 1 # a comment\n", sw.toString());
    }
    
    @Test
    public void printBailOut() {
        Tap13Representer repr = new Tap13Representer();
        BailOut bailtOut = new BailOut("a reason");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, bailtOut);
        assertEquals("Bail out! a reason\n", sw.toString());
    }
    
    @Test
    public void printBailOutWithoutReason() {
        Tap13Representer repr = new Tap13Representer();
        BailOut bailtOut = new BailOut(null);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, bailtOut);
        assertEquals("Bail out!\n", sw.toString());
    }
    
    @Test
    public void printBailOutWithComment() {
        Tap13Representer repr = new Tap13Representer();
        BailOut bailOut = new BailOut(null);
        bailOut.setComment(new Comment("some comment", true));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printTapLine(pw, bailOut);
        assertEquals("Bail out! # some comment\n", sw.toString());
    }
    
    @Test
    public void printFooter() {
        Tap13Representer repr = new Tap13Representer();
        Footer footer = new Footer("footer text");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printFooter(pw, footer);
        assertEquals("TAP footer text\n", sw.toString());
    }
    
    @Test
    public void printFooterWithComment() {
        Tap13Representer repr = new Tap13Representer();
        Footer footer = new Footer("footer text");
        footer.setComment(new Comment("some comment"));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printFooter(pw, footer);
        assertEquals("TAP footer text # some comment\n", sw.toString());
    }
    
    @Test
    public void printPlan() {
        Tap13Representer repr = new Tap13Representer();
        Plan plan = new Plan(10);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printPlan(pw, plan);
        assertEquals("1..10\n", sw.toString());
    }
    
    @Test
    public void printPlanSkipAll() {
        Tap13Representer repr = new Tap13Representer();
        Plan plan = new Plan(10);
        plan.setSkip(new SkipPlan("Any reason"));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printPlan(pw, plan);
        assertEquals("1..10 skip Any reason\n", sw.toString());
    }
    
    @Test(expected = RepresenterException.class)
    public void printPlanEmptyAllowed() {
        DumperOptions options = new DumperOptions();
        options.setAllowEmptyTestPlan(false);
        Tap13Representer repr = new Tap13Representer(options);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printPlan(pw, null);
    }
    
    @Test
    public void printPlanEmptyDisallowed() {
        DumperOptions options = new DumperOptions();
        options.setAllowEmptyTestPlan(true);
        Tap13Representer repr = new Tap13Representer(options);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printPlan(pw, null);
        assertEquals("", sw.toString());
    }
    
    @Test
    public void printHeader() {
        Header header = new Header(13);
        Tap13Representer repr = new Tap13Representer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printHeader(pw, header);
        assertEquals("TAP version 13\n", sw.toString());
    }
    
    @Test
    public void printHeaderWithComment() {
        Header header = new Header(13);
        header.setComment(new Comment("my comment", true));
        Tap13Representer repr = new Tap13Representer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        repr.printHeader(pw, header);
        assertEquals("TAP version 13 # my comment\n", sw.toString());
    }
    
    @Test
    public void printDiagnosticNull() {
        Map<String, Object> diagnostic = null;
        Tap13Representer repr = new Tap13Representer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(diagnostic);
        repr.printDiagnostic(pw, tr);
        assertEquals("", sw.toString());
    }
    
    @Test
    public void printDiagnosticEmpty() {
        Map<String, Object> diagnostic = new HashMap<String, Object>();
        Tap13Representer repr = new Tap13Representer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(diagnostic);
        repr.printDiagnostic(pw, tr);
        assertEquals("", sw.toString());
    }
    
    @Test
    public void printDiagnostic() {
        Map<String, Object> diagnostic = new HashMap<String, Object>();
        diagnostic.put("name", "Ayrton");
        diagnostic.put("surname", "Senna");
        Tap13Representer repr = new Tap13Representer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TestResult tr = new TestResult(StatusValues.OK, 1);
        tr.setDiagnostic(diagnostic);
        repr.printDiagnostic(pw, tr);
        assertEquals("\n  ---\n  name: Ayrton\n  surname: Senna\n  ...\n", sw.toString());
    }
    
}
