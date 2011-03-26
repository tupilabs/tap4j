package br.eti.kinoshita.tap4j.producer;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Binds the required classes to have the integration test between Java and 
 * Perl.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.4.6
 */
public class TapPerlIntegrationModule 
implements Module
{

	/* (non-Javadoc)
	 * @see com.google.inject.Module#configure(com.google.inject.Binder)
	 */
	public void configure( Binder binder )
	{
		binder.bind(TapProducer.class).to(TapProducerImpl.class);
	}

	
	
}
