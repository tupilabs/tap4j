package br.eti.kinoshita.tap4j.junit;

import org.junit.runner.JUnitCore;

import br.eti.kinoshita.tap4j.ext.JUnit40TestTapReporter;

/**
 * Test class for JUnit listener execution 
 * 
 * @author cesar.almeida
 *
 */
public class RunJUnitTestWithListener 
{
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		 JUnitCore core= new JUnitCore();
		 core.addListener(new JUnit40TestTapReporter());
		 core.run(TestTap13JUnit.class);
	}
}
