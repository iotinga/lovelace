package it.netgrid.lovelace;

import org.eclipse.jetty.util.log.Log;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfig extends GuiceServletContextListener {
	
    public static Injector injector;
	
	public GuiceConfig() {
		if(injector!=null){
			Log.getLog().debug("Injector already configured");
		}
	}
	
	public GuiceConfig(Injector _injector) {
		injector = _injector;
	}

	@Override
	protected Injector getInjector() {
		return Main.injector;
	}

}