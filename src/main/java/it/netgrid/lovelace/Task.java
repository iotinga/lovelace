package it.netgrid.lovelace;

import org.quartz.Job;

import it.netgrid.lovelace.model.TaskStatus;

public interface Task extends Job {
	
	public TaskStatus getStatus();
	public int getStepsCount();
	
}
