package it.netgrid.lovelace;

import it.netgrid.lovelace.model.RunStepStatus;

public interface RunStep extends Runnable {
	
	public RunStepStatus getStatus();
	
}
