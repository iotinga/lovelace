package it.netgrid.lovelace.quartz;

import java.sql.SQLException;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.RunReason;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunState;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;

import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RunStatusTriggerListener implements TriggerListener {
	
	private static final Logger log = LoggerFactory.getLogger(RunStatusTriggerListener.class);
	
	private final CrudService<TaskRunStatus, Long> taskRunStatusCrudService;
	
	private final Dao<TaskStatus, Long> taskStatusDao;
	
	@Inject
	public RunStatusTriggerListener(CrudService<TaskRunStatus, Long> taskRunStatusCrudService, Dao<TaskStatus, Long> taskStatusDao) {
		this.taskRunStatusCrudService = taskRunStatusCrudService;
		this.taskStatusDao = taskStatusDao;
	}

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) { }

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) { return false; }

	@Override
	public void triggerMisfired(Trigger trigger) {
		String taskName = trigger.getJobKey().getName();
		log.warn("Misfired trigger for task " + taskName);
		TaskStatus task = this.taskByName(taskName);
		TaskRunStatus runStatus = this.buildRunStatus(task);
		
		try {
			this.taskRunStatusCrudService.create(runStatus);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update run status informations", e);
		} catch (SQLException e) {
			log.error("Unable to update run status informations", e);
		}
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) { }

	private TaskStatus taskByName(String name) {
		try {
			List<TaskStatus> result = this.taskStatusDao.queryForEq(TaskStatus.NAME_FIELD_NAME, name);
			if(result.isEmpty()) return null;
			return result.get(0);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private TaskRunStatus buildRunStatus(TaskStatus task) {
		TaskRunStatus retval = new TaskRunStatus();
		retval.setStartDate(null);
		retval.setState(RunState.END);
		retval.setReason(RunReason.SYSTEM);
		retval.setResult(RunResult.ABORT);
		retval.setTask(task);
		return retval;
	}
}