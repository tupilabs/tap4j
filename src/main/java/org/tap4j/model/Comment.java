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
 * A TAP Comment. In TAP files the comments have a # before the text.
 *
 * @since 1.0
 */
public class Comment extends TapElement {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6694406960961188778L;

    /**
     * Comment text.
     */
    private final String text;

    /**
     * Whether the comment is inline or not.
     */
    private boolean inline;

    /**
     * Constructor with text. A comment must always have a text.
     *
     * @param text Text of the comment.
     */
    public Comment(String text) {
        this(text, false);
    }

    /**
     * Constructor with text. A comment must always have a text.
     *
     * @param text Text of the comment.
     * @param inline Whether the comment is inline or not.
     */
    public Comment(String text, boolean inline) {
        super();
        this.text = text;
        this.inline = inline;
    }

    /**
     * @return Comment text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * @return the inline
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * @param inline the inline to set
     */
    public void setInline(boolean inline) {
        this.inline = inline;
    }

}
