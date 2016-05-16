package it.netgrid.lovelace.tasks;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.netgrid.lovelace.Task;
import it.netgrid.lovelace.model.TaskStatus;

public class SleepingTask implements Task {
	
	private static final Logger log = LoggerFactory.getLogger(SleepingTask.class);
	private static final int DEFAULT_SLEEP_MILLIS = 2000;
	private static final String SLEEP_MILLIS_FIELD_NAME = "sleep_millis";
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			int millis = this.getSleepMillis(arg0);
			Thread.sleep(millis);
			log.info("Slept for " + millis);
		} catch (InterruptedException e) {
			log.debug("Sleep interrupt", e);
		}
	}

	@Override
	public TaskStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStepsCount() {
		return 1;
	}
	
	private int getSleepMillis(JobExecutionContext context) {
		try{
			String millis = context.getJobDetail().getJobDataMap().getString(SLEEP_MILLIS_FIELD_NAME);
			return Integer.parseInt(millis);
		} catch(Exception e) {
			return DEFAULT_SLEEP_MILLIS;
		}
	}

}
