package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.ExecutionResult;
import it.netgrid.lovelace.model.ExecutionState;
import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;

@Singleton
public class StepServiceImpl implements StepService {

	private static final Logger log = LoggerFactory.getLogger(StepServiceImpl.class);
	
	private final CrudService<StepStatus, Long> stepStatusService;
	private final Dao<RunStatus, Long> runStatusDao;
	private final Dao<StepStatus, Long> stepStatusDao;
	private final ConnectionSource connection;
	private final CrudService<RunStatus, Long> runStatusService;
	private final CrudService<TaskStatus, Long> taskStatusService;
	private final Dao<TaskStatus, Long> taskStatusDao;
	
	@Inject
	public StepServiceImpl(
			Dao<TaskStatus, Long> taskStatusDao,
			ConnectionSource connection,
			Dao<RunStatus, Long> runStatusDao,
			Dao<StepStatus, Long> stepStatusDao,
			CrudService<StepStatus, Long> stepStatusService, 
			CrudService<RunStatus, Long> runStatusService, 
			CrudService<TaskStatus, Long> taskStatusService) {
		this.taskStatusDao = taskStatusDao;
		this.stepStatusService = stepStatusService;
		this.runStatusDao = runStatusDao;
		this.stepStatusDao = stepStatusDao;
		this.connection = connection;
		this.runStatusService = runStatusService;
		this.taskStatusService = taskStatusService;
	}
	
	@Override
	public StepStatus start(final TaskStatus task, final String firstStepName, final int totalStepsCount) {
		StepStatus runStatus = null;
		try {
			runStatus = TransactionManager.callInTransaction(connection, new Callable<StepStatus>() {

				@Override
				public StepStatus call() throws Exception {
					RunStatus runStatus = buildRunStatus(task);
					runStatus.setTotalStepsCount(totalStepsCount);

					runStatusService.createRaw(runStatus);
					
					task.setCurrentRun(runStatus);
					taskStatusService.updateRaw(task);
					
					StepStatus stepStatus = buildStepStatus(runStatus, firstStepName);
					stepStatusService.createRaw(stepStatus);
					
					runStatus.setCurrentStep(stepStatus);
					runStatusService.updateRaw(runStatus);
					runStatusDao.refresh(stepStatus.getRunStatus());

					return stepStatus;
				}
				
			});
		} catch (SQLException e) {
			log.error("Unable to update task status", e);
		}
		
		return runStatus;
	}

	@Override
	public StepStatus nextStep(TaskStatus task, ExecutionResult currentStepResult, String nextStepName) {
		StepStatus runStatus = null;
		try {
			runStatus = TransactionManager.callInTransaction(connection, new Callable<StepStatus>() {

				@Override
				public StepStatus call() throws Exception {
					runStatusDao.refresh(task.getCurrentRun());
					RunStatus runStatus = task.getCurrentRun();
					StepStatus oldStepStatus = getCurrentStep(runStatus);
					end(oldStepStatus, currentStepResult);
					StepStatus stepStatus = buildStepStatus(runStatus, nextStepName);
					stepStatusService.createRaw(stepStatus);
					runStatus.setCurrentStep(stepStatus);
					runStatusService.updateRaw(runStatus);
					stepStatus = stepStatusService.read(stepStatus.getId());
					runStatusDao.refresh(stepStatus.getRunStatus());
					return stepStatus;
				}
				
			});
		} catch (SQLException e) {
			log.error("Unable to update task status", e);
		}
		return runStatus;
	}

	@Override
	public StepStatus end(final TaskStatus task, final ExecutionResult currentStepResult, final ExecutionResult taskResult) {
		StepStatus runStatus = null;
		try {
			runStatus = TransactionManager.callInTransaction(connection, new Callable<StepStatus>() {

				@Override
				public StepStatus call() throws Exception {
					runStatusDao.refresh(task.getCurrentRun());
					RunStatus run = task.getCurrentRun();
					StepStatus stepStatus = getCurrentStep(run); 
					end(stepStatus, currentStepResult);
					run.setEndDate(new Date());
					run.setResult(taskResult);
					run.setState(ExecutionState.END);
					run.setCurrentStep(null);
					runStatusService.updateRaw(run);
					
					task.setLastRun(run);
					task.setCurrentRun(null);
					if(taskResult == ExecutionResult.SUCCESS) {
						task.setLastSuccessRun(run);
					}
					taskStatusService.updateRaw(task);
					return stepStatusService.read(stepStatus.getId());
				}
				
			});
		} catch (SQLException e) {
			log.error("Unable to update task status", e);
		}
		
		return runStatus;
	}
	
	private StepStatus getCurrentStep(RunStatus runStatus) {
		QueryBuilder<StepStatus, Long> query = this.stepStatusDao.queryBuilder();
		try {
			query.where().eq(StepStatus.RUN_STATUS_ID_FIELD_NAME, runStatus.getId());
			List<StepStatus> runSteps = query.orderBy(StepStatus.START_TIME_FIELD_NAME, false).query();
			if(runSteps.isEmpty()) return null;
			return runSteps.get(0);
		} catch (SQLException e) {
			return null;
		}
	}
	
	private void end(StepStatus step, ExecutionResult result) throws IllegalArgumentException, SQLException {
		this.stepStatusDao.refresh(step);
		step.setEndTime(new Date());
		step.setState(ExecutionState.END);
		step.setResult(result);
		this.stepStatusService.updateRaw(step);
	}
	
	private StepStatus buildStepStatus(RunStatus runStatus, String stepName) {
		StepStatus runStep = new StepStatus();
		runStep.setName(stepName);
		runStep.setRunStatus(runStatus);
		runStep.setStartTime(new Date());
		runStep.setState(ExecutionState.RUN);
		return runStep;
	}
	
	private RunStatus buildRunStatus(TaskStatus task) {
		RunStatus retval = new RunStatus();
		retval.setStartDate(new Date());
		retval.setState(ExecutionState.RUN);
		retval.setTaskStatus(task);
		return retval;
	}

	@Override
	public StepStatus nextStep(JobExecutionContext context, ExecutionResult currentStepResult, String nextStepName) {
		TaskStatus task = this.getTaskStatus(context);
		return this.nextStep(task, currentStepResult, nextStepName);
	}

	@Override
	public TaskStatus getTaskStatus(JobExecutionContext context) {
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
