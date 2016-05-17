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
}
