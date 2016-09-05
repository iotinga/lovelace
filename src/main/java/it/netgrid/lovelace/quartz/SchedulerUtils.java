package it.netgrid.lovelace.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class SchedulerUtils {

	private static final Logger log = LoggerFactory.getLogger(SchedulerUtils.class);
	
	private static final String TRIGGER_NAME_FORMAT = "%s#TRIGGER";
	
	private final Configuration config;
	private final Scheduler scheduler;
	
	@Inject
	public SchedulerUtils(Scheduler scheduler, Configuration config) {
		this.config = config;
		this.scheduler = scheduler;
	}

	public Trigger getTrigger(TaskStatus task) {
		Trigger retval = newTrigger()
			.withIdentity(this.getTriggerKey(task))
			.startNow()
			.withSchedule(cronSchedule(task.getSchedule()))
			.forJob(this.getJobKey(task))
			.build();
		return retval;
	}
	
	public TriggerKey getTriggerKey(TaskStatus task) {
		TriggerKey retval = new TriggerKey(this.getTriggerName(task), this.config.getQuartzGroupName());
		return retval;
	}
	
	public String getTriggerName(TaskStatus task) {
		return String.format(TRIGGER_NAME_FORMAT, task.getName());
	}
	
	public JobKey getJobKey(TaskStatus task) {
		JobKey retval = new JobKey(task.getName(), this.config.getQuartzGroupName());
		return retval;
	}
	
	public boolean runNow(TaskStatus task) {
		try {
			this.scheduler.triggerJob(this.getJobKey(task));
			return true;
		} catch (SchedulerException e) {
			log.error("Unable to trigger task", e);
			return false;
		}
	}
	
	public boolean stopNow(TaskStatus task) {
		try {
			this.scheduler.interrupt(this.getJobKey(task));
			return true;
		} catch (UnableToInterruptJobException e) {
			log.error("Unable to interrupt task", e);
			return false;
		}
	}
}
