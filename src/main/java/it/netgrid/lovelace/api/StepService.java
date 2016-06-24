package it.netgrid.lovelace.api;

import org.quartz.JobExecutionContext;

import it.netgrid.lovelace.model.ExecutionResult;
import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.lovelace.model.TaskStatus;

public interface StepService {
	public StepStatus start(TaskStatus task, String firstStepName, int totalStepsCount);
	public StepStatus nextStep(JobExecutionContext context, ExecutionResult currentStepResult, String nextStepName);
	public StepStatus nextStep(TaskStatus task, ExecutionResult currentStepResult, String nextStepName);
	public StepStatus end(TaskStatus task, ExecutionResult stepResult, ExecutionResult result);
	public TaskStatus getTaskStatus(JobExecutionContext context);
}
