package it.netgrid.lovelace;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class Application extends ResourceConfig {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final String PACKAGE_NAME = Application.class.getPackageName();
	private static final String[] RESOURCES_PACKAGES = new String[] {
            PACKAGE_NAME + ".rest"
    };

	
    public Application() {
        // Set package to look for resources in
        packages(RESOURCES_PACKAGES);
        register(GuiceFeature.class);
        register(JaxrsOpenApiScanner.class);
        register(SwaggerSerializers.class);
        register(OpenApiResource.class);
        register(FreemarkerMvcFeature.class);
    }
}