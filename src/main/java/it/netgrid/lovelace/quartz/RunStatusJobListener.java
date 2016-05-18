package it.netgrid.lovelace.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.netgrid.lovelace.api.RunStatusService;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class RunStatusJobListener implements JobListener {
	
	private static final String START_STEP_NAME = "start";
	
	private final RunStatusService service;

	@Inject
	public RunStatusJobListener(RunStatusService service) {
		this.service = service;
	}
	
	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		TaskStatus task = this.service.getTaskStatus(context);
		this.service.start(task, START_STEP_NAME);
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		TaskStatus task = this.service.getTaskStatus(context);
		this.service.start(task, START_STEP_NAME);
		this.service.end(task, RunResult.ABORT, RunResult.ABORT);
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		TaskStatus task = this.service.getTaskStatus(context);
		RunResult result = jobException == null ? RunResult.SUCCESS : RunResult.ERROR;
		this.service.end(task, result, result);
	}
}
