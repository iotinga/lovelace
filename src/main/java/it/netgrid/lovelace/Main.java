package it.netgrid.lovelace;

import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import com.google.inject.servlet.GuiceServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
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
import jakarta.inject.Singleton;
import com.google.inject.servlet.ServletModule;

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
			Main.injector = Main.getInjector();
			// Create the server
			Main.server = new Server(new InetSocketAddress(Main.config.getBindAddress(), Main.config.getBindPort()));
			Main.scheduler.setJobFactory(Main.injector.getInstance(GuiceJobFactory.class));

			GuiceServletContextListener guiceListener = new GuiceServletContextListener() {
				private static Injector defaultInjector;

				@Override
				protected Injector getInjector() {
					return getDefaultInjector();
				}

				public static Injector getDefaultInjector() {
					return Main.injector;
				}
			};
			// Setup web app context
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath("/");
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			webAppContext.setResourceBase(classloader.getResource("webapp").toString());
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
	
	protected static Injector getInjector() {
		  
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
		  Main.modules.add(
				  new ServletModule() {
					  @Override
					  protected void configureServlets() {

						  Map<String, String> params = new HashMap<String, String>();
						  params.put("jakarta.ws.rs.Application", Application.class.getCanonicalName());
						  params.put("jersey.config.server.mvc.templateBasePath.freemarker", "layout");
						  params.put("jersey.config.servlet.filter.contextPath", "/*");
						  params.put("gzip", "true");
						  params.put("useFileMappedBuffer","true");
						  params.put("redirectWelcome","true");
						  params.put("dirAllowed","true");

						  bind(ServletContainer.class).in(Singleton.class);
						  filter("/static/*").through(StaticFilter.class);
						  serve("/rest/*").with(ServletContainer.class, params);

						  // This bindings must be after the "serve" call
						  bind(SQLExceptionMapper.class);
						  bind(IllegalArgumentExceptionMapper.class);
						  bind(NullPointerExceptionMapper.class);
					  }
				  }
		  );
		  return Guice.createInjector(
				  Main.modules
		  );
	}

	public static Injector getDefaultInjector(){
		return Main.injector;
	}

}
