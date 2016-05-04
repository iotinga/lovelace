package it.netgrid.lovelace;

import java.util.List;

import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.model.TaskStepStatus;

public interface Task extends Runnable {
	
	public TaskStatus getStatus();
	public List<TaskStepStatus> getStepsStatus();
	
}
