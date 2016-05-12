package it.netgrid.lovelace;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedHTTPServer {
	
	private static final Logger log = LoggerFactory.getLogger(EmbeddedHTTPServer.class);
	private static final String RESOURCES_PACKAGE = "it.netgrid.lovelace.rest";
	private static Configuration config;
	
	public static void main(String[] args) throws IOException {
		if(args.length > 1) {
			config = new PropertiesConfigurationImpl(args[1]);
		} else {
			config = new PropertiesConfigurationImpl();
		}
		
		Server httpServer = EmbeddedHTTPServer.create(config);
		
		try {
			httpServer.start();
		} catch (Exception e) {
			log.warn("Unable to start", e);
		}
		
		if(httpServer.isRunning() || httpServer.isStarting()) {
			boolean run = true;
			while(run) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					log.debug("Server shutdown.", e);
					run = false;
				}
			}
			// LOOP CATCHING SIGTERM
			
			try {
				httpServer.stop();
				httpServer.join();
			} catch (InterruptedException e) {
				log.warn("Dirty stop", e);
			} catch (Exception e) {
				log.warn("Unable to stop", e);
			}
		}
		
	}
	
	private static Server create(Configuration config) {
		URI baseUri = UriBuilder.fromUri(config.getBindAddress()).port(config.getBindPort()).build();
		ResourceConfig resources = new ResourceConfig();
		resources.packages(RESOURCES_PACKAGE);
		return JettyHttpContainerFactory.createServer(baseUri, resources);
	}
}
