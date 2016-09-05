package it.netgrid.lovelace.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.netgrid.lovelace.Task;
import it.netgrid.lovelace.api.StepService;
import it.netgrid.lovelace.model.ExecutionResult;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class RunStatusJobListener implements JobListener {
	
	private final StepService service;

	@Inject
	public RunStatusJobListener(StepService service) {
		this.service = service;
	}
	
	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		TaskStatus status = this.service.getTaskStatus(context);
		Task task = (Task)context.getJobInstance();
		this.service.start(status, task.getFirstStepName(), task.getStepsCount());
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		TaskStatus status = this.service.getTaskStatus(context);
		Task task = (Task)context.getJobInstance();
		this.service.start(status, task.getFirstStepName(), task.getStepsCount());
		this.service.end(status, ExecutionResult.ABORT, ExecutionResult.ABORT);
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		TaskStatus task = this.service.getTaskStatus(context);
		ExecutionResult result = jobException == null ? ExecutionResult.SUCCESS : ExecutionResult.ERROR;
		this.service.end(task, result, result);
	}
}
