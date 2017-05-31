package it.netgrid.lovelace;

import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

import it.netgrid.lovelace.api.ApiModule;
import it.netgrid.lovelace.model.DaoModule;
import it.netgrid.lovelace.model.JdbcConnectionModule;
import it.netgrid.lovelace.quartz.GuiceJobFactory;
import it.netgrid.lovelace.quartz.LovelaceSchedulerListener;
import it.netgrid.lovelace.quartz.RunStatusJobListener;
import it.netgrid.lovelace.quartz.RunStatusTriggerListener;
import it.netgrid.lovelace.rest.IllegalArgumentExceptionMapper;
import it.netgrid.lovelace.rest.NullPointerExceptionMapper;
import it.netgrid.lovelace.rest.SQLExceptionMapper;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static Configuration config;
	private static Scheduler scheduler;
	private static Server server;
	private static Injector injector;
	private static List<Module> modules;
	
	public static void main(String[] args) throws IOException {
		
		if(args.length > 1) {
			Main.config = new PropertiesConfigurationImpl(args[1]);
		} else {
			Main.config = new PropertiesConfigurationImpl();
		}
		Main.modules = new ArrayList<>();
		Main.run();
	}
	
	public static void mainWithInjectableModulesAndConfig(List<Module> modules, Configuration config) throws IOException {
		Main.config = config;
		Main.modules = modules;
		Main.run();
	}
	
	private static void run() throws IOException {
		try {
			// Create scheduler
			Main.scheduler = StdSchedulerFactory.getDefaultScheduler();
			Main.injector = Main.createInjector();
			Main.scheduler.setJobFactory(Main.injector.getInstance(GuiceJobFactory.class));
			
			// Create the server
			Main.server = new Server(new InetSocketAddress(Main.config.getBindAddress(), Main.config.getBindPort()));
			JerseyGuiceUtils.install(injector);
			
			// Setup web app context
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath("/");
		    
			// Initialize servlet
		    /*
		     *     <servlet>
			 *	        <servlet-name>Lovelace</servlet-name>
			 *	        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
			 *	        <init-param>
			 *	            <param-name>javax.ws.rs.Application</param-name>
			 *	            <param-value>it.netgrid.lovelace.Application</param-value>
			 *	        </init-param>
			 *	        <load-on-startup>1</load-on-startup>
			 *	    </servlet>
			 *	    <servlet-mapping>
			 *	        <servlet-name>Lovelace</servlet-name>
			 *	        <url-pattern>/*</url-pattern>
			 *	    </servlet-mapping>
		     */
		    ServletHolder sh = webAppContext.addServlet(org.glassfish.jersey.servlet.ServletContainer.class,"/*");
		    sh.setInitParameter("javax.ws.rs.Application", "it.netgrid.lovelace.Application");
		    
		    // Initialize filters
		    /*
		     * 
		     *         <filter>
			 *		        <filter-name>guice-filter</filter-name>
			 *		        <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
			 *		    </filter>
		     * 
		     */
		    // addFilter returns FilterHolder object that can be used to specify initialitaion parameters of this filter
		    webAppContext.addFilter(com.google.inject.servlet.GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		    
		    // Setup listener
		    /*
		     *     <listener>
			 *	        <listener-class>it.netgrid.lovelace.GuiceConfig</listener-class>
			 *	    </listener>
		     */
		    webAppContext.addEventListener(Main.injector.getInstance(GuiceConfig.class));
		    
			/* Important: Use getResource */
		    //String webxmlLocation = Main.class.getResource("/webapp/WEB-INF/web.xml").toString();
		    //webAppContext.setDescriptor("WEB-INF/web.xml");

		    /* Important: Use getResource */
		    //String resLocation = Main.class.getResource("/webapp").toString();
			//webAppContext.setResourceBase("src//webapp");
		    //webAppContext.setParentLoaderPriority(true);
		    // System temp directory as resource base. This package doesn't provide resources
		    webAppContext.setResourceBase(System.getProperty("java.io.tmpdir"));
		    
		    server.setHandler(webAppContext);
	
			// Start services
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
	
	protected static Scheduler getScheduler() {
		return Main.scheduler;
	}
	
	protected static Injector createInjector() {

		  Main.modules.add(new JerseyGuiceModule("__HK2_Generated_0"));
		  Main.modules.add(new ServletModule() {
			 @Override
			 protected void configureServlets() {
				// This bindings must be after the "serve" call
				bind(SQLExceptionMapper.class);
				bind(IllegalArgumentExceptionMapper.class);
				bind(NullPointerExceptionMapper.class);
			 }
		  });
		  
		  if( ! Main.config.hasJdbcConnectionReuse()) {
			  Main.modules.add(new JdbcConnectionModule());
		  }
		  
		  Main.modules.add(new DaoModule());
		  Main.modules.add(new ApiModule());
		  Main.modules.add(new AbstractModule() {
		    @Override
		    protected void configure() {

		    }
		    
		    @Provides
			@Singleton
			public Configuration getConfig() {
				return config;
			}


			@Provides
			@Singleton
			public Scheduler getScheduler(LovelaceSchedulerListener schedulerListener, RunStatusJobListener jobListener, RunStatusTriggerListener triggerListener) throws SchedulerException {
				Main.scheduler.getListenerManager().addSchedulerListener(schedulerListener);
				Main.scheduler.getListenerManager().addJobListener(jobListener, groupEquals(config.getQuartzGroupName()));
				Main.scheduler.getListenerManager().addTriggerListener(triggerListener, groupEquals(config.getQuartzGroupName()));
				return Main.scheduler;
			}
			
			@Provides
			@Singleton
			public Server getServer() {
				return server;
			}
		  });
		
		return Guice.createInjector(Main.modules);
	}
}
