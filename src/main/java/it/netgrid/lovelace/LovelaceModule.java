package it.netgrid.lovelace;

import org.quartz.Scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class LovelaceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Configuration.class).to(PropertiesConfigurationImpl.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	public Scheduler getScheduler() {
		return Main.getScheduler();
	}
}
