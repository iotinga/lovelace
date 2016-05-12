package it.netgrid.lovelace;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class LovelaceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Configuration.class).to(PropertiesConfigurationImpl.class).in(Singleton.class);
	}

}
