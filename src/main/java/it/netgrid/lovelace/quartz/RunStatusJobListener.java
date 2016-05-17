package it.netgrid.lovelace.quartz;

import java.sql.SQLException;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

import it.netgrid.lovelace.api.RunStatusService;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class RunStatusJobListener implements JobListener {
	
	private static final Logger log = LoggerFactory.getLogger(RunStatusJobListener.class);
	
	private static final String START_STEP_NAME = "start";
	
	private final RunStatusService service;
	private final Dao<TaskStatus, Long> taskStatusDao;

	@Inject
	public RunStatusJobListener(RunStatusService service, 
			Dao<TaskStatus, Long> taskStatusDao) {
		this.service = service;
		this.taskStatusDao = taskStatusDao;
	}
	
	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		TaskStatus task = this.taskByContext(context);
		try {
			this.service.start(task, START_STEP_NAME);
		} catch (SQLException e) {
			log.warn("Unable to set task status", e);
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		TaskStatus task = this.taskByContext(context);
		try {
			this.service.start(task, START_STEP_NAME);
			this.service.end(task, RunResult.ABORT, RunResult.ABORT);
		} catch (SQLException e) {
			log.warn("Unable to set task status", e);
		}
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		TaskStatus task = this.taskByContext(context);
		try {
			RunResult result = jobException == null ? RunResult.SUCCESS : RunResult.ERROR;
			this.service.end(task, result, result);
		} catch (SQLException e) {
			log.error("Unable to retrieve current run status");
			return;
		}
		
	}

	private TaskStatus taskByContext(JobExecutionContext context) {
		try {
			List<TaskStatus> result = this.taskStatusDao.queryForEq(TaskStatus.NAME_FIELD_NAME, context.getJobDetail().getKey().getName());
			if(result.isEmpty()) return null;
			return result.get(0);
		} catch (SQLException e) {
			log.warn("Unable to load task status from context", e);
			return null;
		}
	}
}
