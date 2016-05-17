package it.netgrid.lovelace.api;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import io.codearte.jfairy.Fairy;
import it.netgrid.lovelace.LovelaceTestEnv;
import it.netgrid.lovelace.PersistenceTestHandler;
import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunState;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class RunStatusServiceImplTest {
	
	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(LovelaceTestEnv.class);
	
	@Inject
	private Fairy fairy;
	
	@Inject
	private PersistenceTestHandler persistence;
	
	@Inject
	private RunStatusServiceImpl classUnderTest;
	
	@Inject
	private Dao<TaskStatus, Long> taskStatusDao;
	
	@Inject
	private Dao<TaskRunStatus, Long> taskRunDao;
	
	@Inject
	private Dao<RunStepStatus, Long> runStepDao;
	
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
		RunStepStatus step = this.classUnderTest.start(task, "start");
	
		task = this.taskStatusDao.queryForId((long)1);
		TaskRunStatus runStatus = taskRunDao.queryForId(step.getRunStatus().getId());
		
		assertEquals(runStatus.getId(), task.getCurrentRun().getId());
	}
	
	@Test
	public void testRunStepStatusCreationOnStart() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		RunStepStatus step = this.classUnderTest.start(task, "start");
		
		RunStepStatus newStep = this.runStepDao.queryForId(step.getId());
		
		assertNotNull(newStep);
		assertNotNull(newStep.getRunStatus());
		assertNotNull(newStep.getRunStatus().getId());
	}
	
	@Test
	public void testNextStepNewStepCreation() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		RunStepStatus currentStep = this.classUnderTest.start(task, "start");
		
		RunResult result = fairy.baseProducer().randomElement(RunResult.values());
		String stepName = fairy.textProducer().latinSentence();
		RunStepStatus nextStep = this.classUnderTest.nextStep(task, result, stepName);
		
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
		RunStepStatus currentStep = this.classUnderTest.start(task, "start");
		
		RunResult result = fairy.baseProducer().randomElement(RunResult.values());
		String stepName = fairy.textProducer().latinSentence();
		this.classUnderTest.nextStep(task, result, stepName);
		
		RunStepStatus oldStep = this.runStepDao.queryForId(currentStep.getId());
		
		assertEquals(result, oldStep.getResult());
	}
	
	@Test
	public void testEndTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		RunStepStatus firstStep = this.classUnderTest.start(task, "start");
		taskRunDao.refresh(task.getCurrentRun());
		TaskRunStatus currentRun = task.getCurrentRun();

		RunResult taskResult = fairy.baseProducer().randomElement(RunResult.values());
		RunResult stepResult = fairy.baseProducer().randomElement(RunResult.values());
		RunStepStatus lastStep = this.classUnderTest.end(task, stepResult, taskResult);
		
		task = this.taskStatusDao.queryForId((long)1);
		taskRunDao.refresh(task.getLastRun());
		TaskRunStatus lastRun = task.getLastRun();
		

		assertNull(task.getCurrentRun());
		assertEquals(currentRun.getId(), lastRun.getId());
		assertEquals(RunState.END, lastRun.getState());
		
		assertEquals(firstStep.getId(), lastStep.getId());
		assertEquals(stepResult, lastStep.getResult());
		assertEquals(taskResult, task.getLastRun().getResult());
	}
	
	@Test
	public void testEndSuccessfulTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		this.classUnderTest.start(task, "start");

		this.classUnderTest.end(task, RunResult.SUCCESS, RunResult.SUCCESS);
		
		task = this.taskStatusDao.queryForId((long)1);
		taskRunDao.refresh(task.getLastRun());
		taskRunDao.refresh(task.getLastSuccessRun());
		
		assertNotNull(task.getLastSuccessRun());
		assertEquals(task.getLastRun().getId(), task.getLastSuccessRun().getId());
	}

	
	@Test
	public void testEndErrorTask() throws SQLException {
		TaskStatus task = this.taskStatusDao.queryForId((long)1);
		this.classUnderTest.start(task, "start");

		this.classUnderTest.end(task, RunResult.ERROR, RunResult.ERROR);
		
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
