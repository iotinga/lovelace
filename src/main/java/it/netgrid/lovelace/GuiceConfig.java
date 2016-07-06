package it.netgrid.lovelace;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfig extends GuiceServletContextListener {
	
    private final  Injector injector;
	
    @Inject
	public GuiceConfig(Injector _injector) {
		injector = _injector;
	}

	@Override
	protected Injector getInjector() {
		return injector;
	}

}