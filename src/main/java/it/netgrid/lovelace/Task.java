package it.netgrid.lovelace;

import java.util.List;

import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.model.RunStepStatus;

public interface Task extends Runnable {
	
	public TaskStatus getStatus();
	public List<RunStepStatus> getStepsStatus();
	
}
