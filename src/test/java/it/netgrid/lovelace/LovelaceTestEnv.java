package it.netgrid.lovelace;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.codearte.jfairy.Fairy;
import it.netgrid.lovelace.api.ApiModule;
import it.netgrid.lovelace.model.ModelModule;

public class LovelaceTestEnv extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceBerryModule());
		install(new ModelModule());
		install(new ApiModule());
		
		bind(PersistenceTestHandler.class).to(FullRandomPersistenceTestHandler.class).in(Singleton.class);
	}
	
	@Provides
	@Singleton
	public Scheduler getScheduler() throws SchedulerException {
		return StdSchedulerFactory.getDefaultScheduler();
	}

	@Provides
	@Singleton
	public Configuration getConfig() {
		return new Configuration() {
			
			@Override
			public String getQuartzGroupName() {
				return "global";
			}
			
			@Override
			public Properties getProperties() {
				return null;
			}
			
			@Override
			public String getJdbcUsername() {
				return "root";
			}
			
			@Override
			public String getJdbcPassword() {
				return "root";
			}
			
			@Override
			public String getJdbcConnectionUrl() {
				return "jdbc:h2:mem:lovelace?zeroDateTimeBehavior=convertToNull";
			}
			
			@Override
			public int getBindPort() {
				return 9099;
			}
			
			@Override
			public String getBindAddress() {
				return "127.0.0.1";
			}

			@Override
			public Long getSystemId() {
				return (long)1;
			}
		};
	}
	
	@Provides
	public Fairy getFairy() {
		return Fairy.create();
	}
}
