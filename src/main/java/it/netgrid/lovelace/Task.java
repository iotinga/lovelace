package it.netgrid.lovelace;

import org.quartz.InterruptableJob;

public interface Task extends InterruptableJob {
	
	public String getFirstStepName();
	public int getStepsCount();
	
}
