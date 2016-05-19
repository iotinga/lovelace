package it.netgrid.lovelace;

import it.netgrid.lovelace.model.StepStatus;

public interface RunStep extends Runnable {
	
	public StepStatus getStatus();
	
}
