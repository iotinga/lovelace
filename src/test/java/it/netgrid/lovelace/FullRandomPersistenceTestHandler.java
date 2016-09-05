package it.netgrid.lovelace;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import io.codearte.jfairy.Fairy;
import it.netgrid.commons.SerializableUtils;
import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.tasks.SleepingTask;

public class FullRandomPersistenceTestHandler implements PersistenceTestHandler {

	private static final String TRIGGER_NAME_FORMAT = "%s#TRIGGER";
	
	private final Dao<StepStatus, Long> runStepStatusDao;
	private final Dao<SchedulerStatus, Long> systemStatusDao;
	private final Dao<RunStatus, Long> taskRunStatusDao;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final ConnectionSource connectionSource;
	private final Scheduler scheduler;
	private final Fairy fairy;
	private final Configuration config;
	
	@Inject
	public FullRandomPersistenceTestHandler( 
			ConnectionSource connectionSource,
			Dao<StepStatus, Long> runStepStatusDao,
			Dao<SchedulerStatus, Long> systemStatusDao,
			Dao<RunStatus, Long> taskRunStatusDao,
			Dao<TaskStatus, Long> taskStatusDao, 
			Fairy fairy,
			Scheduler scheduler,
			Configuration config) {
		this.runStepStatusDao = runStepStatusDao;
		this.systemStatusDao = systemStatusDao;
		this.taskRunStatusDao = taskRunStatusDao;
		this.taskStatusDao = taskStatusDao;
		this.connectionSource = connectionSource;
		this.fairy = fairy;
		this.config = config;
		this.scheduler = scheduler;
	}

	@Override
	public void setup() {
		try {
			TableUtils.createTableIfNotExists(connectionSource, StepStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, SchedulerStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, RunStatus.class);
			TableUtils.createTableIfNotExists(connectionSource, TaskStatus.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		try {
			TableUtils.dropTable(connectionSource, StepStatus.class, true);
			TableUtils.dropTable(connectionSource, SchedulerStatus.class, true);
			TableUtils.dropTable(connectionSource, RunStatus.class, true);
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
		SchedulerStatus system = new SchedulerStatus();
		system.setActiveFrom(new Date());
		this.systemStatusDao.create(system);
		this.createTask(system);
	}
	
	public void createTask(SchedulerStatus system) throws SQLException {
		Map<String, String> config = new HashMap<String, String>();
		config.put(SleepingTask.SLEEP_MILLIS_FIELD_NAME, "10000");
		String configString = SerializableUtils.serializeBase64(config);
		TaskStatus task = new TaskStatus();
		task.setCanonicalName("it.netgrid.lovelace.tasks.SleepingTask");
		task.setCreation(new Date());
		task.setConfig(config);
		task.setMarshalledConfig(configString);
		task.setName(this.fairy.textProducer().latinSentence());
		task.setSchedule("0 0 12 1/1 * ? *");
		task.setSchedulerStatus(system);
		task.setUpdated(new Date());
		this.taskStatusDao.create(task);
		
		Trigger trigger = this.getTrigger(task);
		JobDetail detail = this.getJobDetail(task);
		try {
			this.scheduler.scheduleJob(detail, trigger);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException("INVALID_SCHEDULER",e);
		}
	}
	private Trigger getTrigger(TaskStatus task) {
		Trigger retval = newTrigger()
			.withIdentity(this.getTriggerKey(task))
			.startNow()
			.withSchedule(cronSchedule(task.getSchedule()))
			.forJob(this.getJobKey(task))
			.build();
		return retval;
	}
	
	@SuppressWarnings("unchecked")
	private JobDetail getJobDetail(TaskStatus task) {
		Class<? extends Job> clazz = null;
		try {
			clazz = (Class<? extends Job>) Class.forName(task.getCanonicalName());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("INVALID_CANONICAL_NAME", e);
		}
		
		JobDataMap dataMap = new JobDataMap(task.getConfig());
		JobDetail retval = newJob(clazz).withIdentity(this.getJobKey(task)).setJobData(dataMap).build();
		return retval;
	}
	
	private TriggerKey getTriggerKey(TaskStatus task) {
		TriggerKey retval = new TriggerKey(this.getTriggerName(task), this.config.getQuartzGroupName());
		return retval;
	}
	
	private String getTriggerName(TaskStatus task) {
		return String.format(TRIGGER_NAME_FORMAT, task.getName());
	}
	
	private JobKey getJobKey(TaskStatus task) {
		JobKey retval = new JobKey(task.getName(), this.config.getQuartzGroupName());
		return retval;
	}
}
