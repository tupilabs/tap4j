/**
 * 
 */
package br.eti.kinoshita.tap4j;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class TestTapConsumer {

	protected TAPLexer lexer;
	protected TAPParser parser;
	
	@BeforeMethod
	public void setUp() 
	throws Exception
	{
		lexer = new TAPLexer(new ANTLRStringStream("# OI\r\n" + 
				"# outro comentario"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		parser = new TAPParser(tokens);
	}
	
	@Test
	public void testParser() 
	throws Exception
	{
		parser.tapFile();
		System.out.println( parser.commentsList );
	}
	
}
