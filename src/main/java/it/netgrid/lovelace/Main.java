package it.netgrid.lovelace;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	private static Configuration config;
	
	public static void main(String[] args) throws IOException {
		if(args.length > 1) {
			config = new PropertiesConfigurationImpl(args[1]);
		} else {
			config = new PropertiesConfigurationImpl();
		}
		
		// Create the server
		Server server = new Server(new InetSocketAddress(config.getBindAddress(), config.getBindPort()));

		// Create a servlet context and add the jersey servlet
		ServletContextHandler sch = new ServletContextHandler(server, "/");

		// Add our Guice listener that includes our bindings
		sch.addEventListener(new GuiceConfig());

		// Then add GuiceFilter and configure the server to
		// reroute all requests through this filter.
		sch.addFilter(GuiceFilter.class, "/*", null);

		// Must add DefaultServlet for embedded Jetty.
		// Failing to do this will cause 404 errors.
		sch.addServlet(DefaultServlet.class, "/");

		// Start the server
		try {
			server.start();
			server.join();
		} catch (InterruptedException e) {
			log.warn("Server shutdown", e);
		} catch (Exception e) {
			log.warn("Runtime error", e);
		}
	}
}
