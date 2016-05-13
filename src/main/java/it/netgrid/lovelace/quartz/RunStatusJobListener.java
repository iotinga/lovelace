package it.netgrid.lovelace.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RunStatusJobListener implements JobListener {

	@Inject
	public RunStatusJobListener() {
		
	}
	
	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		// TODO Auto-generated method stub
		
	}

}
