package br.eti.kinoshita.tap4j.ext;

import org.junit.runner.Description;

public class JUnitTestData {
	private Description description;
	private Boolean ignored;
	private Boolean failed;
	private String failMessage;
	private String failTrace;
	private Throwable failException;

	public JUnitTestData(Boolean ignored, Boolean failed) {
		this.ignored = ignored;
		this.failed = failed;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public Boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(Boolean ignored) {
		this.ignored = ignored;
	}

	public Boolean isFailed() {
		return failed;
	}

	public void setFailed(Boolean failed) {
		this.failed = failed;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public String getFailTrace() {
		return failTrace;
	}

	public void setFailTrace(String failTrace) {
		this.failTrace = failTrace;
	}

	public Throwable getFailException() {
		return failException;
	}

	public void setFailException(Throwable failException) {
		this.failException = failException;
	}
}