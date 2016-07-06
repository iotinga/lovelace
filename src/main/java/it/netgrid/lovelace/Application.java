package it.netgrid.lovelace;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.google.inject.Inject;

public class Application extends ResourceConfig {

	@Inject
    public Application(ServiceLocator serviceLocator, GuiceConfig config) {
        // Set package to look for resources in
        packages(Main.RESOURCES_PACKAGES);

        System.out.println("Registering injectables...");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(config.getInjector());
    }
}