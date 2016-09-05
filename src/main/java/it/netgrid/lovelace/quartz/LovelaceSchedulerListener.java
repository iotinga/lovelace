package it.netgrid.lovelace.quartz;

import java.sql.SQLException;
import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.SchedulerStatus;

@Singleton
public class LovelaceSchedulerListener implements SchedulerListener {
	
	private final static Logger log = LoggerFactory.getLogger(LovelaceSchedulerListener.class);
	
	private final CrudService<SchedulerStatus, Long> schedulerStatusService;
	private final Configuration config;

	@Inject
	public LovelaceSchedulerListener(
			Configuration config,
			CrudService<SchedulerStatus, Long> schedulerStatusService) {
		this.schedulerStatusService = schedulerStatusService;
		this.config = config;
	}
	
	@Override
	public void jobScheduled(Trigger trigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersPaused(String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersResumed(String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobPaused(JobKey jobKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobsPaused(String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobResumed(JobKey jobKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobsResumed(String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerStarted() {
		try {
			SchedulerStatus status = this.schedulerStatusService.read(this.config.getSchedulerId());
			status.setActiveFrom(new Date());
			this.schedulerStatusService.update(status);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update scheduler status");
		} catch (SQLException e) {
			log.error("Unable to update scheduler status");
		}
	}

	@Override
	public void schedulerStarting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShuttingdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulingDataCleared() {
		// TODO Auto-generated method stub

	}

}
