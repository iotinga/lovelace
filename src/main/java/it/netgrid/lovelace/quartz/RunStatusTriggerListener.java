package it.netgrid.lovelace.quartz;

import java.sql.SQLException;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.j256.ormlite.dao.Dao;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.ExecutionResult;
import it.netgrid.lovelace.model.ExecutionState;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;

import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RunStatusTriggerListener implements TriggerListener {
	
	private static final Logger log = LoggerFactory.getLogger(RunStatusTriggerListener.class);
	
	private final CrudService<RunStatus, Long> runStatusService;
	
	private final Dao<TaskStatus, Long> taskStatusDao;
	
	@Inject
	public RunStatusTriggerListener(CrudService<RunStatus, Long> runStatusService, Dao<TaskStatus, Long> taskStatusDao) {
		this.runStatusService = runStatusService;
		this.taskStatusDao = taskStatusDao;
	}

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) { 
		log.debug("Trigger fired: " + trigger.getKey().getName());
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) { return false; }

	@Override
	public void triggerMisfired(Trigger trigger) {
		String taskName = trigger.getJobKey().getName();
		log.warn("Misfired trigger for task " + taskName);
		TaskStatus task = this.taskByName(taskName);
		RunStatus runStatus = this.buildRunStatus(task);
		
		try {
			this.runStatusService.create(runStatus);
		} catch (IllegalArgumentException e) {
			log.error("Unable to update run status informations", e);
		} catch (SQLException e) {
			log.error("Unable to update run status informations", e);
		}
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
		log.debug("Trigger complete: " + trigger.getKey().getName());
	}

	private TaskStatus taskByName(String name) {
		try {
			List<TaskStatus> result = this.taskStatusDao.queryForEq(TaskStatus.NAME_FIELD_NAME, name);
			if(result.isEmpty()) return null;
			return result.get(0);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private RunStatus buildRunStatus(TaskStatus task) {
		RunStatus retval = new RunStatus();
		retval.setStartDate(null);
		retval.setState(ExecutionState.END);
		retval.setResult(ExecutionResult.ABORT);
		retval.setTaskStatus(task);
		return retval;
	}
}
