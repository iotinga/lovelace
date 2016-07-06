package it.netgrid.lovelace;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Application extends ResourceConfig {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String RESOURCES_PACKAGES = "it.netgrid.lovelace.rest";
	
	@Inject
    public Application(ServiceLocator serviceLocator, GuiceConfig config) {
        // Set package to look for resources in
        packages(RESOURCES_PACKAGES);

        log.debug("Registering injectables...");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(config.getInjector());
    }
}