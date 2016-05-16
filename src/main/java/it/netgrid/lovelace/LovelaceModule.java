package it.netgrid.lovelace;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import static org.quartz.impl.matchers.GroupMatcher.*;

import it.netgrid.lovelace.quartz.LovelaceSchedulerListener;
import it.netgrid.lovelace.quartz.RunStatusJobListener;
import it.netgrid.lovelace.quartz.RunStatusTriggerListener;

public class LovelaceModule extends AbstractModule {

	@Override
	protected void configure() {
		// NOTHING TO DO
	}

	@Provides
	@Singleton
	public Scheduler getScheduler(Configuration config, 
			RunStatusJobListener jobListener, 
			RunStatusTriggerListener triggerListener,
			LovelaceSchedulerListener schedulerListener) throws SchedulerException {
		Scheduler scheduler = Main.getScheduler();
		scheduler.getListenerManager().addSchedulerListener(schedulerListener);
		scheduler.getListenerManager().addJobListener(jobListener, groupEquals(config.getQuartzGroupName()));
		scheduler.getListenerManager().addTriggerListener(triggerListener, groupEquals(config.getQuartzGroupName()));
		return scheduler;
	}
}
