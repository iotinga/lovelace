package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunState;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class RunStatusServiceImpl implements RunStatusService {

	private final CrudService<RunStepStatus, Long> runStepCrudService;
	private final Dao<TaskRunStatus, Long> taskRunStatusDao;
	private final Dao<RunStepStatus, Long> runStepDao;
	private final ConnectionSource connection;
	private final CrudService<TaskRunStatus, Long> taskRunStatusCrudService;
	private final CrudService<TaskStatus, Long> taskStatusCrudService;
	
	@Inject
	public RunStatusServiceImpl(
			ConnectionSource connection,
			Dao<TaskRunStatus, Long> taskRunStatusDao,
			Dao<RunStepStatus, Long> runStepDao,
			CrudService<RunStepStatus, Long> runStepCrudService, 
			CrudService<TaskRunStatus, Long> taskRunStatusCrudService, 
			CrudService<TaskStatus, Long> taskStatusCrudService) {
		this.runStepCrudService = runStepCrudService;
		this.taskRunStatusDao = taskRunStatusDao;
		this.runStepDao = runStepDao;
		this.connection = connection;
		this.taskRunStatusCrudService = taskRunStatusCrudService;
		this.taskStatusCrudService = taskStatusCrudService;
	}
	
	@Override
	public RunStepStatus start(final TaskStatus task, final String firstStepName) throws SQLException {
		RunStepStatus runStatus = TransactionManager.callInTransaction(connection, new Callable<RunStepStatus>() {

			@Override
			public RunStepStatus call() throws Exception {
				TaskRunStatus runStatus = buildRunStatus(task);
				taskRunStatusCrudService.createRaw(runStatus);
				task.setCurrentRun(runStatus);
				taskStatusCrudService.updateRaw(task);
				RunStepStatus stepStatus = buildRunStepStatus(runStatus, firstStepName);
				runStepCrudService.createRaw(stepStatus);
				runStatus.setCurrentStep(stepStatus);
				taskRunStatusCrudService.updateRaw(runStatus);
				stepStatus = runStepCrudService.read(stepStatus.getId());
				taskRunStatusDao.refresh(stepStatus.getRunStatus());
				return stepStatus;
			}
			
		});
		
		return runStatus;
	}

	@Override
	public RunStepStatus nextStep(TaskStatus task, RunResult currentStepResult, String nextStepName) throws SQLException {
		RunStepStatus runStatus = TransactionManager.callInTransaction(connection, new Callable<RunStepStatus>() {

			@Override
			public RunStepStatus call() throws Exception {
				taskRunStatusDao.refresh(task.getCurrentRun());
				TaskRunStatus runStatus = task.getCurrentRun();
				RunStepStatus oldStepStatus = getCurrentStep(runStatus);
				end(oldStepStatus);
				RunStepStatus stepStatus = buildRunStepStatus(runStatus, nextStepName);
				runStepCrudService.createRaw(stepStatus);
				runStatus.setCurrentStep(stepStatus);
				taskRunStatusCrudService.updateRaw(runStatus);
				stepStatus = runStepCrudService.read(stepStatus.getId());
				taskRunStatusDao.refresh(stepStatus.getRunStatus());
				return stepStatus;
			}
			
		});
		return runStatus;
	}

	@Override
	public RunStepStatus end(final TaskStatus task, final RunResult result) throws SQLException {
		RunStepStatus runStatus = TransactionManager.callInTransaction(connection, new Callable<RunStepStatus>() {

			@Override
			public RunStepStatus call() throws Exception {
				taskRunStatusDao.refresh(task.getCurrentRun());
				TaskRunStatus taskRun = task.getCurrentRun();
				RunStepStatus stepStatus = getCurrentStep(taskRun); 
				end(stepStatus);
				taskRun.setEndDate(new Date());
				taskRun.setResult(result);
				taskRun.setState(RunState.END);
				taskRunStatusCrudService.updateRaw(taskRun);
				
				task.setLastRun(taskRun);
				task.setCurrentRun(null);
				if(result == RunResult.SUCCESS) {
					task.setLastSuccessRun(taskRun);
				}
				taskStatusCrudService.updateRaw(task);
				return runStepCrudService.read(stepStatus.getId());
			}
			
		});
		
		return runStatus;
	}
	
	private RunStepStatus getCurrentStep(TaskRunStatus runStatus) {
		QueryBuilder<RunStepStatus, Long> query = this.runStepDao.queryBuilder();
		try {
			query.where().eq(RunStepStatus.TASK_RUN_STATUS_ID_FIELD_NAME, runStatus.getId());
			List<RunStepStatus> runSteps = query.orderBy(RunStepStatus.START_TIME_FIELD_NAME, false).query();
			if(runSteps.isEmpty()) return null;
			return runSteps.get(0);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private void end(RunStepStatus step) throws IllegalArgumentException, SQLException {
		step.setEndTime(new Date());
		step.setStatus(RunState.END);
		this.runStepCrudService.updateRaw(step);
	}
	
	private RunStepStatus buildRunStepStatus(TaskRunStatus runStatus, String stepName) {
		RunStepStatus runStep = new RunStepStatus();
		runStep.setName(stepName);
		runStep.setRunStatus(runStatus);
		runStep.setStartTime(new Date());
		runStep.setStatus(RunState.RUN);
		return runStep;
	}
	
	private TaskRunStatus buildRunStatus(TaskStatus task) {
		TaskRunStatus retval = new TaskRunStatus();
		retval.setStartDate(new Date());
		retval.setState(RunState.RUN);
		retval.setTask(task);
		return retval;
	}
}