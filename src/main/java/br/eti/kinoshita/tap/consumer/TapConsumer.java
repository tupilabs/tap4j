/**
 * 
 */
package br.eti.kinoshita.tap.consumer;

import java.io.File;

/**
 * 
 * @author Bruno P. Kinoshita
 *
 */
public interface TapConsumer 
{

	public void consume( File tapFile ) 
	throws TapConsumerException;
	
	public void consume( String tapFile ) 
	throws TapConsumerException;
	
	
	
}
