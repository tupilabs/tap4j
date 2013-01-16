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

import java.util.LinkedList;
import java.util.List;

import org.tap4j.util.StatusValues;

/**
 * A simple test result. Valid values are <em>OK</em> and <em>NOT OK</em>.
 *
 * @since 1.0
 */
public class TestResult extends TapResult {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -2735372334488828166L;

    /**
     * Test Status (OK, NOT OK).
     */
    private StatusValues status;

    /**
     * Test Number.
     */
    private Integer testNumber;

    /**
     * Description of the test.
     */
    private String description;

    /**
     * Directive of the test (TO DO, SKIP).
     */
    private Directive directive;

    /**
     * Child subtest.
     */
    private TestSet subtest;

    /**
     * Comment.
     */
    private List<Comment> comments;

    /**
     * Default constructor.
     */
    public TestResult() {
        super();
        this.status = StatusValues.NOT_OK;
        this.testNumber = -1;
        this.subtest = null;
        this.comments = new LinkedList<Comment>();
    }

    /**
     * Constructor with parameter.
     *
     * @param testStatus Status of the test.
     * @param testNumber Number of the test.
     */
    public TestResult(StatusValues testStatus, Integer testNumber) {
        super();
        this.status = testStatus;
        this.testNumber = testNumber;
        this.comments = new LinkedList<Comment>();
    }

    /**
     * @return Status of the test.
     */
    public StatusValues getStatus() {
        return this.status;
    }

    /**
     * @param status Status of the test.
     */
    public void setStatus(StatusValues status) {
        this.status = status;
    }

    /**
     * @return Test Number.
     */
    public Integer getTestNumber() {
        return this.testNumber;
    }

    /**
     * @param testNumber Test Number.
     */
    public void setTestNumber(Integer testNumber) {
        this.testNumber = testNumber;
    }

    /**
     * @return Test description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description Test description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Optional Directive.
     */
    public Directive getDirective() {
        return this.directive;
    }

    /**
     * @param directive Optional Directive.
     */
    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    /**
     * @return the subtest
     */
    public TestSet getSubtest() {
        return subtest;
    }

    /**
     * @param subtest the subtest to set
     */
    public void setSubtest(TestSet subtest) {
        this.subtest = subtest;
    }

    /**
     * @return The comments for this Test Result.
     */
    public List<Comment> getComments() {
        return this.comments;
    }

    /**
     * @param comments list of comments for this Test Result.
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Adds a new comment to this Test Result.
     *
     * @param comment comment for this Test Result.
     */
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

}
