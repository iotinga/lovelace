package it.netgrid.lovelace;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import io.codearte.jfairy.Fairy;
import it.netgrid.commons.SerializableUtils;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.SystemStatus;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.tasks.SleepingTask;

public class FullRandomPersistenceTestHandler implements PersistenceTestHandler {
	
	private final Dao<RunStepStatus, Long> runStepStatusDao;
	private final Dao<SystemStatus, Long> systemStatusDao;
	private final Dao<TaskRunStatus, Long> taskRunStatusDao;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final ConnectionSource connectionSource;
	private final Fairy fairy;
	
	@Inject
	public FullRandomPersistenceTestHandler( 
			ConnectionSource connectionSource,
			Dao<RunStepStatus, Long> runStepStatusDao,
			Dao<SystemStatus, Long> systemStatusDao,
			Dao<TaskRunStatus, Long> taskRunStatusDao,
			Dao<TaskStatus, Long> taskStatusDao, 
			Fairy fairy) {
		this.runStepStatusDao = runStepStatusDao;
		this.systemStatusDao = systemStatusDao;
		this.taskRunStatusDao = taskRunStatusDao;
		this.taskStatusDao = taskStatusDao;
		this.connectionSource = connectionSource;
		this.fairy = fairy;
	}

	@Override
	public void setup() {
		try {
			TableUtils.createTableIfNotExists(connectionSource, RunStepStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, SystemStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, TaskRunStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, TaskStatus.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		try {
			TableUtils.dropTable(connectionSource, RunStepStatus.class, true);
			TableUtils.dropTable(connectionSource, SystemStatus.class, true);
			TableUtils.dropTable(connectionSource, TaskRunStatus.class, true);
			TableUtils.dropTable(connectionSource, TaskStatus.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadData() {
		try {
			this.createSystem();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createSystem() throws SQLException {
		SystemStatus system = new SystemStatus();
		system.setActiveFrom(new Date());
		system.setUptime(BigDecimal.TEN);
		this.systemStatusDao.create(system);
		this.createTask(system);
	}
	
	public void createTask(SystemStatus system) throws SQLException {
		Map<String, String> config = new HashMap<String, String>();
		config.put(SleepingTask.SLEEP_MILLIS_FIELD_NAME, "10000");
		String configString = SerializableUtils.serializeBase64(config);
		TaskStatus task = new TaskStatus();
		task.setCanonicalName("it.netgrid.lovelace.tasks.SleepingTask");
		task.setCreation(new Date());
		task.setConfig(config);
		task.setMarshalledConfig(configString);
		task.setName(this.fairy.textProducer().latinSentence());
		task.setSchedule(this.fairy.textProducer().latinSentence());
		task.setSystemStatus(system);
		task.setUpdated(new Date());
		this.taskStatusDao.create(task);
	}

}
