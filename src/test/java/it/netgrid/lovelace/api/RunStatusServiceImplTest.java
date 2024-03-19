package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.google.guiceberry.junit4.GuiceBerryRule;
import jakarta.inject.Inject;
import com.j256.ormlite.dao.Dao;

import com.devskiller.jfairy.Fairy;
import it.netgrid.lovelace.LovelaceTestEnv;
import it.netgrid.lovelace.PersistenceTestHandler;
import it.netgrid.lovelace.model.ExecutionResult;
import it.netgrid.lovelace.model.ExecutionState;
import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class RunStatusServiceImplTest {
	
	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(LovelaceTestEnv.class);
	
	@Inject
	private Fairy fairy;
	
	@Inject
	private PersistenceTestHandler persistence;
	
	@Inject
	private StepServiceImpl classUnderTest;
	
	@Inject
	private Dao<TaskStatus, Long> taskStatusDao;
	
	@Inject
	private Dao<RunStatus, Long> taskRunDao;
	
	@Inject
	private Dao<StepStatus, Long> runStepDao;
	
	@Before
	public void setUp() {
		persistence.setup();
		persistence.loadData();
	}
	
	@After
	public void tearDown() {
		persistence.destroy();
	}
	
	@Test
	public void testRunStatusCreationOnStart() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		StepStatus step = this.classUnderTest.start(task, "start", 1);
	
		task = this.taskStatusDao.queryForId((long)1);
		RunStatus runStatus = taskRunDao.queryForId(step.getRunStatus().getId());
		
		assertEquals(runStatus.getId(), task.getCurrentRun().getId());
	}
	
	@Test
	public void testRunStepStatusCreationOnStart() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		StepStatus step = this.classUnderTest.start(task, "start", 1);
		
		StepStatus newStep = this.runStepDao.queryForId(step.getId());
		
		assertNotNull(newStep);
		assertNotNull(newStep.getRunStatus());
		assertNotNull(newStep.getRunStatus().getId());
	}
	
	@Test
	public void testNextStepNewStepCreation() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		StepStatus currentStep = this.classUnderTest.start(task, "start", 1);
		
		ExecutionResult result = ExecutionResult.valueOf(fairy.baseProducer().randomElement(Arrays.toString(ExecutionResult.values())));
		String stepName = fairy.textProducer().latinSentence();
		StepStatus nextStep = this.classUnderTest.nextStep(task, result, stepName);
		
		taskStatusDao.refresh(task);
		taskRunDao.refresh(task.getCurrentRun());
		
		assertNotNull(nextStep.getId());
		assertNotEquals(currentStep.getId(), nextStep.getId());
		assertEquals(stepName, nextStep.getName());
		assertEquals(nextStep.getId(), task.getCurrentRun().getCurrentStep().getId());

	}
	
	@Test
	public void testNextStepOldStepClosed() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		StepStatus currentStep = this.classUnderTest.start(task, "start", 1);
		
		ExecutionResult result = ExecutionResult.valueOf(fairy.baseProducer().randomElement(Arrays.toString(ExecutionResult.values())));
		String stepName = fairy.textProducer().latinSentence();
		this.classUnderTest.nextStep(task, result, stepName);
		
		StepStatus oldStep = this.runStepDao.queryForId(currentStep.getId());
		
		assertEquals(result, oldStep.getResult());
	}
	
	@Test
	public void testEndTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		StepStatus firstStep = this.classUnderTest.start(task, "start", 1);
		taskRunDao.refresh(task.getCurrentRun());
		RunStatus currentRun = task.getCurrentRun();

		ExecutionResult taskResult = ExecutionResult.valueOf(fairy.baseProducer().randomElement(Arrays.toString(ExecutionResult.values())));
		ExecutionResult stepResult = ExecutionResult.valueOf(fairy.baseProducer().randomElement(Arrays.toString(ExecutionResult.values())));
		StepStatus lastStep = this.classUnderTest.end(task, stepResult, taskResult);
		
		task = this.taskStatusDao.queryForId((long)1);
		taskRunDao.refresh(task.getLastRun());
		RunStatus lastRun = task.getLastRun();
		

		assertNull(task.getCurrentRun());
		assertEquals(currentRun.getId(), lastRun.getId());
		assertEquals(ExecutionState.END, lastRun.getState());
		
		assertEquals(firstStep.getId(), lastStep.getId());
		assertEquals(stepResult, lastStep.getResult());
		assertEquals(taskResult, task.getLastRun().getResult());
	}
	
	@Test
	public void testEndSuccessfulTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		this.classUnderTest.start(task, "start", 1);

		this.classUnderTest.end(task, ExecutionResult.SUCCESS, ExecutionResult.SUCCESS);
		
		task = this.taskStatusDao.queryForId((long)1);
		taskRunDao.refresh(task.getLastRun());
		taskRunDao.refresh(task.getLastSuccessRun());
		
		assertNotNull(task.getLastSuccessRun());
		assertEquals(task.getLastRun().getId(), task.getLastSuccessRun().getId());
	}

	
	@Test
	public void testEndErrorTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		this.classUnderTest.start(task, "start", 1);

		this.classUnderTest.end(task, ExecutionResult.ERROR, ExecutionResult.ERROR);
		
		task = this.taskStatusDao.queryForId((long)1);
		taskRunDao.refresh(task.getLastRun());
		taskRunDao.refresh(task.getLastSuccessRun());
		
		if(task.getLastSuccessRun() == null) {
			assertNotNull(task.getLastRun());
		} else {
			assertNotEquals(task.getLastRun().getId(), task.getLastSuccessRun().getId());
		}
	}
}
