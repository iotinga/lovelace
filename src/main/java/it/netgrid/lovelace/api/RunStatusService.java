package it.netgrid.lovelace.api;

import org.quartz.JobExecutionContext;

import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskStatus;

public interface RunStatusService {
	public RunStepStatus start(TaskStatus task, String firstStepName, int totalStepsCount);
	public RunStepStatus nextStep(JobExecutionContext context, RunResult currentStepResult, String nextStepName);
	public RunStepStatus nextStep(TaskStatus task, RunResult currentStepResult, String nextStepName);
	public RunStepStatus end(TaskStatus task, RunResult stepResult, RunResult result);
	public TaskStatus getTaskStatus(JobExecutionContext context);
}
