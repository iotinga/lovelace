package it.netgrid.lovelace;

import java.util.List;

import org.quartz.Job;

import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.model.RunStepStatus;

public interface Task extends Job {
	
	public TaskStatus getStatus();
	public List<RunStepStatus> getStepsStatus();
	
}
