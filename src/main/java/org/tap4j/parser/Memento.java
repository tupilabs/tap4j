/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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
package org.tap4j.parser;

import org.tap4j.model.TapElement;
import org.tap4j.model.TestSet;

/**
 * Memento for parsers.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class Memento {

	private boolean firstLine;
	private boolean planBeforeTestResult;
	private String lastLine;
	private TapElement lastParsedElement;
	/**
	 * Indicator of the base indentation level. Usually defined by the TAP
	 * Header.
	 */
	private int baseIndentationLevel;
	/**
	 * Helper indicator of in what indentantion level we are working at moment.
	 * It is helpful specially when you have many nested elements, like a META
	 * element with some multiline text.
	 */
	private int currentIndentationLevel;
	private boolean currentlyInYaml;
	private boolean currentlyInSubtest;
	private StringBuilder diagnosticBuffer = new StringBuilder();
	private TestSet testSet;

	public Memento() {
		super();
	}

	/**
	 * @return if is first line
	 */
	public boolean isFirstLine() {
		return firstLine;
	}

	/**
	 * @param is
	 *            first line value
	 */
	public void setFirstLine(boolean firstLine) {
		this.firstLine = firstLine;
	}

	/**
	 * @return the planBeforeTestResult
	 */
	public boolean isPlanBeforeTestResult() {
		return planBeforeTestResult;
	}

	/**
	 * @param planBeforeTestResult
	 *            the planBeforeTestResult to set
	 */
	public void setPlanBeforeTestResult(boolean planBeforeTestResult) {
		this.planBeforeTestResult = planBeforeTestResult;
	}

	/**
	 * @return the lastLine
	 */
	public String getLastLine() {
		return lastLine;
	}

	/**
	 * @param lastLine
	 *            the lastLine to set
	 */
	public void setLastLine(String lastLine) {
		this.lastLine = lastLine;
	}

	/**
	 * @return the lastParsedElement
	 */
	public TapElement getLastParsedElement() {
		return lastParsedElement;
	}

	/**
	 * @param lastParsedElement
	 *            the lastParsedElement to set
	 */
	public void setLastParsedElement(TapElement lastParsedElement) {
		this.lastParsedElement = lastParsedElement;
	}

	/**
	 * @return the baseIndentationLevel
	 */
	public int getBaseIndentationLevel() {
		return baseIndentationLevel;
	}

	/**
	 * @param baseIndentationLevel
	 *            the baseIndentationLevel to set
	 */
	public void setBaseIndentationLevel(int baseIndentationLevel) {
		this.baseIndentationLevel = baseIndentationLevel;
	}

	/**
	 * @return the currentIndentationLevel
	 */
	public int getCurrentIndentationLevel() {
		return currentIndentationLevel;
	}

	/**
	 * @param currentIndentationLevel
	 *            the currentIndentationLevel to set
	 */
	public void setCurrentIndentationLevel(int currentIndentationLevel) {
		this.currentIndentationLevel = currentIndentationLevel;
	}

	/**
	 * @return the currentlyInYaml
	 */
	public boolean isCurrentlyInYaml() {
		return currentlyInYaml;
	}

	/**
	 * @param currentlyInYaml
	 *            the currentlyInYaml to set
	 */
	public void setCurrentlyInYaml(boolean currentlyInYaml) {
		this.currentlyInYaml = currentlyInYaml;
	}

	/**
	 * @return the currentlyInSubtest
	 */
	public boolean isCurrentlyInSubtest() {
		return currentlyInSubtest;
	}

	/**
	 * @param currentlyInSubtest
	 *            the currentlyInSubtest to set
	 */
	public void setCurrentlyInSubtest(boolean currentlyInSubtest) {
		this.currentlyInSubtest = currentlyInSubtest;
	}

	/**
	 * @return the diagnosticBuffer
	 */
	public StringBuilder getDiagnosticBuffer() {
		return diagnosticBuffer;
	}

	/**
	 * @param diagnosticBuffer
	 *            the diagnosticBuffer to set
	 */
	public void setDiagnosticBuffer(StringBuilder diagnosticBuffer) {
		this.diagnosticBuffer = diagnosticBuffer;
	}

	/**
	 * @return the testSet
	 */
	public TestSet getTestSet() {
		return testSet;
	}

	/**
	 * @param testSet
	 *            the testSet to set
	 */
	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

}
