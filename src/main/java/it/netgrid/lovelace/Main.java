package it.netgrid.lovelace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import it.netgrid.lovelace.api.ApiModule;
import it.netgrid.lovelace.model.ModelModule;
import it.netgrid.lovelace.quartz.GuiceJobFactory;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static final String RESOURCES_PACKAGES = "it.netgrid.lovelace.rest";
	private static Configuration config;
	private static Scheduler scheduler;
	private static Server server;
	private static Injector injector;
	
	public static void main(String[] args) throws IOException {
		if(args.length > 1) {
			Main.config = new PropertiesConfigurationImpl(args[1]);
		} else {
			Main.config = new PropertiesConfigurationImpl();
		}
		
		Main.injector = Main.buildInjector();
		
		try {
			// Create scheduler
			Main.scheduler = StdSchedulerFactory.getDefaultScheduler();
			Main.scheduler.setJobFactory(Main.injector.getInstance(GuiceJobFactory.class));
			
			// Create the server
			Main.server = new Server(new InetSocketAddress(Main.config.getBindAddress(), Main.config.getBindPort()));

			// Create a servlet context and add the jersey servlet
			ServletContextHandler sch = new ServletContextHandler(server, "/");
	
			// Add our Guice listener that includes our bindings
			sch.addEventListener(new GuiceConfig(Main.injector));
	
			// Then add GuiceFilter and configure the server to
			// reroute all requests through this filter.
			sch.addFilter(GuiceFilter.class, "/*", null);
	
			// Must add DefaultServlet for embedded Jetty.
			// Failing to do this will cause 404 errors.
			sch.addServlet(DefaultServlet.class, "/");
	
			// Start the server
			try {
				Main.scheduler.start();
				Main.server.start();
				Main.server.join();
				Main.scheduler.shutdown();
			} catch (InterruptedException e) {
				log.warn("Server shutdown", e);
			} catch (Exception e) {
				log.warn("Runtime error", e);
			}

		} catch (SchedulerException e) {
			log.error("Cannot init scheduler", e);
		}
	}
	
	public static Scheduler getScheduler() {
		return Main.scheduler;
	}
	
	public static Server getServer() {
		return Main.server;
	}
	
	public static Injector buildInjector() {
		return Guice.createInjector(new JerseyServletModule() {
					
			@Override
			protected void configureServlets() {
				install(new LovelaceModule());
				install(new ModelModule());
				install(new ApiModule());

				// Set init params for Jersey
				Map<String, String> params = new HashMap<String, String>();
				params.put("com.sun.jersey.config.property.packages", RESOURCES_PACKAGES);
				params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

				// Route all requests through GuiceContainer
				serve("/*").with(GuiceContainer.class, params);
			}
			
			@Provides
			@Singleton
			public Configuration getConfig() {
				return config;
			}
					
		});
	}
}
