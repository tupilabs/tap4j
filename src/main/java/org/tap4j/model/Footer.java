/*
 * The MIT License
 *
 * Copyright (c) <2010> <tap4j>
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
 * This class represents a Footer in the TAP Stream. A Footer, or tail is a mere
 * line with some text. In the very beginning there is a 'TAP' token and then
 * the text. For instance, 'TAP done' is a valid Footer.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Footer extends AbstractTapElementDiagnostic {
	private static final long serialVersionUID = 6941329821890027928L;

	/**
	 * Footer text.
	 */
	private String text;

	/**
	 * Comment.
	 */
	private Comment comment;

	/**
	 * Constructor that calls super class constructor.
	 * 
	 * @param text
	 *            Footer text
	 */
	public Footer(String text) {
		super();
		this.text = text;
	}

	/**
	 * @return Footer's text.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param comment
	 *            Comment.
	 */
	public void setComment(Comment comment) {
		this.comment = comment;
	}

	/**
	 * @return Comment.
	 */
	public Comment getComment() {
		return this.comment;
	}

}
