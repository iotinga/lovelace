package it.netgrid.lovelace.tasks;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.netgrid.lovelace.Task;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class SleepingTask implements Task {
	
	private static final Logger log = LoggerFactory.getLogger(SleepingTask.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			Thread.sleep(6000);
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
	public List<RunStepStatus> getStepsStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
