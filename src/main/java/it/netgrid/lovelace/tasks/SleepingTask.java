package it.netgrid.lovelace.tasks;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import it.netgrid.lovelace.Task;
import it.netgrid.lovelace.api.RunStatusService;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.TaskStatus;

@DisallowConcurrentExecution
public class SleepingTask implements Task {
	
	private static final Logger log = LoggerFactory.getLogger(SleepingTask.class);
	private static final int DEFAULT_SLEEP_MILLIS = 2000;
	public static final String SLEEP_MILLIS_FIELD_NAME = "sleep_millis";
	
	private final RunStatusService runStatus;
	
	@Inject
	public SleepingTask(RunStatusService runStatus) {
		this.runStatus = runStatus;
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			int millis = this.getSleepMillis(arg0);
			this.runStatus.nextStep(arg0, RunResult.SUCCESS, "going to sleep");
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
		return 2;
	}
	
	private int getSleepMillis(JobExecutionContext context) {
		try{
			String millis = context.getJobDetail().getJobDataMap().getString(SLEEP_MILLIS_FIELD_NAME);
			return Integer.parseInt(millis);
		} catch(Exception e) {
			return DEFAULT_SLEEP_MILLIS;
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.debug("Job interrupt");
	}

}
