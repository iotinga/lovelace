package it.netgrid.lovelace.quartz;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunState;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class RunStatusJobListener implements JobListener {
	
	private static final Logger log = LoggerFactory.getLogger(RunStatusJobListener.class);
	
	private final CrudService<TaskRunStatus, Long> taskRunStatusCrudService;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final Dao<TaskRunStatus, Long> taskRunStatusDao;
	private final CrudService<TaskStatus, Long> taskStatusCrudService;

	@Inject
	public RunStatusJobListener(CrudService<TaskRunStatus, Long> taskRunStatusCrudService, 
			Dao<TaskStatus, Long> taskStatusDao,
			Dao<TaskRunStatus, Long> taskRunStatusDao,
			CrudService<TaskStatus, Long> taskStatusCrudService) {
		this.taskRunStatusCrudService = taskRunStatusCrudService;
		this.taskStatusDao = taskStatusDao;
		this.taskRunStatusDao = taskRunStatusDao;
		this.taskStatusCrudService = taskStatusCrudService;
	}
	
	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		TaskStatus task = this.taskByContext(context);
		TaskRunStatus runStatus = this.buildRunStatus(task);
		try {
			this.taskRunStatusCrudService.create(runStatus);
			task.setCurrentRun(runStatus);
			this.taskStatusCrudService.update(task);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update run status informations", e);
		} catch (SQLException e) {
			log.error("Unable to update run status informations", e);
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		TaskStatus task = this.taskByContext(context);
		TaskRunStatus runStatus = this.buildRunStatus(task);
		runStatus.setEndDate(new Date());
		runStatus.setState(RunState.END);
		runStatus.setResult(RunResult.ABORT);
		try {
			this.taskRunStatusCrudService.create(runStatus);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update run status informations", e);
		} catch (SQLException e) {
			log.error("Unable to update run status informations", e);
		}
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		TaskStatus task = this.taskByContext(context);
		try {
			this.taskRunStatusDao.refresh(task.getCurrentRun());
		} catch (SQLException e) {
			log.error("Unable to retrieve current run status");
			return;
		}
		
		TaskRunStatus runStatus = task.getCurrentRun();
		runStatus.setEndDate(new Date());
		runStatus.setState(RunState.END);
		if(jobException == null) {
			runStatus.setResult(RunResult.SUCCESS);
			task.setLastSuccessRun(runStatus);
		} else {
			runStatus.setResult(RunResult.ERROR);
		}

		task.setLastRun(runStatus);
		task.setCurrentRun(null);
		
		try {
			this.taskRunStatusCrudService.update(runStatus);
			this.taskStatusCrudService.update(task);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update run status informations", e);
		} catch (SQLException e) {
			log.error("Unable to update run status informations", e);
		}
		
	}

	private TaskStatus taskByContext(JobExecutionContext context) {
		try {
			List<TaskStatus> result = this.taskStatusDao.queryForEq(TaskStatus.NAME_FIELD_NAME, context.getJobDetail().getKey().getName());
			if(result.isEmpty()) return null;
			return result.get(0);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private TaskRunStatus buildRunStatus(TaskStatus task) {
		TaskRunStatus retval = new TaskRunStatus();
		retval.setStartDate(new Date());
		retval.setState(RunState.RUN);
		retval.setTask(task);
		return retval;
	}
}
