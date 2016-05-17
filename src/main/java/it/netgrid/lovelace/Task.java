package it.netgrid.lovelace;

import org.quartz.InterruptableJob;

import it.netgrid.lovelace.model.TaskStatus;

public interface Task extends InterruptableJob {
	
	public TaskStatus getStatus();
	public int getStepsCount();
	
}
