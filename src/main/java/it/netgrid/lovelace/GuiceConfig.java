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
//		return Guice.createInjector(new JerseyServletModule() {
//			
//			@Override
//			protected void configureServlets() {
//				install(new LovelaceModule());
//				install(new ModelModule());
//				install(new ApiModule());
//
//				// Set init params for Jersey
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("com.sun.jersey.config.property.packages", RESOURCES_PACKAGES);
//				params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
//
//				// Route all requests through GuiceContainer
//				serve("/*").with(GuiceContainer.class, params);
//			}
//			
//		});
	}

}