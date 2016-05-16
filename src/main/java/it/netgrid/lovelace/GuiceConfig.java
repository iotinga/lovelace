package it.netgrid.lovelace;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfig extends GuiceServletContextListener {
	
	private final Injector injector;
	
	public GuiceConfig(Injector injector) {
		this.injector = injector;
	}

	@Override
	protected Injector getInjector() {
		return injector;
	}

}