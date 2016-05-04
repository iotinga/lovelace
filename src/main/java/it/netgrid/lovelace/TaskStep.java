package it.netgrid.lovelace;

import it.netgrid.lovelace.model.TaskStepStatus;

public interface TaskStep extends Runnable {
	
	public TaskStepStatus getStatus();
	
}
