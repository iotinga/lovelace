package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

import com.cronutils.validator.CronValidator;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.SerializableUtils;
import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class TaskStatusCrudService extends TemplateCrudService<TaskStatus, Long> {

	public static final String INVALID_NAME = "name";
	public static final String INVALID_CANONICAL_NAME = "canonical_name";
	public static final String INVALID_SCHEDULER = "scheduler";
	public static final String INVALID_SCHEDULER_JOB_DETAILS = "scheduler/job_details";
	public static final String INVALID_SCHEDULER_TRIGGER = "scheduler/trigger";
	public static final String QUARTZ_GROUP_NAME = "global";
	private static final String TRIGGER_NAME_FORMAT = "%s#TRIGGER";
	
	private final Scheduler scheduler;
	private final CronValidator cronValidator;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final Dao<TaskRunStatus, Long> taskRunStatusDao;
	private final CrudService<TaskRunStatus, Long> taskRunStatusCrudService;
	
	@Inject
	public TaskStatusCrudService(ConnectionSource connection, 
			Scheduler scheduler, 
			CronValidator cronValidator,
			Dao<TaskStatus, Long> taskStatusDao,
			Dao<TaskRunStatus, Long> taskRunStatusDao,
			CrudService<TaskRunStatus, Long> taskRunStatusCrudService) {
		super(connection);
		this.scheduler = scheduler;
		this.cronValidator = cronValidator;
		this.taskStatusDao = taskStatusDao;
		this.taskRunStatusDao = taskRunStatusDao;
		this.taskRunStatusCrudService = taskRunStatusCrudService;
	}

	@Override
	public int createRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		Validate.notBlank(task.getName());
		Validate.notNull(task.getSystemStatus());
		this.cronValidator.validate(task.getSchedule());
		
		task.setCreation(new Date());
		task.setMarshalledConfig(this.getConfigString(task.getConfig()));
		task.setUpdated(task.getCreation());
		
		int retval = this.taskStatusDao.create(task);
		
		JobDetail detail = this.getJobDetail(task);
		Trigger trigger = this.getTrigger(task);

		// Scheduler operations have to been treated as "COMMIT"s:
		// Scheduler can rollback DB operations, 
		// DB will not rollback scheduler operations
		try {
			this.scheduler.scheduleJob(detail, trigger);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER);
		}

		return retval;
	}

	@Override
	public int deleteRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		int retval = 0;
		for(TaskRunStatus run : task.getTaskRuns()) {
			retval += this.taskRunStatusCrudService.deleteRaw(run);
		}
		
		retval += this.taskStatusDao.delete(task);

		// Scheduler operations have to been treated as "COMMIT"s:
		// Scheduler can rollback DB operations, 
		// DB will not rollback scheduler operations
		try {
			this.scheduler.deleteJob(this.getJobKey(task));
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER);
		}
		
		return retval;
	}

	@Override
	public int updateRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		TaskStatus oldTask = this.taskStatusDao.queryForId(task.getId());
		if(!task.getName().equals(oldTask.getName())) {
			throw new IllegalArgumentException(INVALID_NAME);
		}
		if(!task.getCanonicalName().equals(oldTask.getCanonicalName())) {
			throw new IllegalArgumentException(INVALID_CANONICAL_NAME);
		}
		
		int retval = 0;
		try {
			// Updating Job Details
			JobDetail jobDetail = this.scheduler.getJobDetail(this.getJobKey(task));
			jobDetail.getJobDataMap().clear();
			jobDetail.getJobDataMap().putAll(task.getConfig());
			this.scheduler.addJob(jobDetail, true);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER_JOB_DETAILS);
		}

		try {
			// Updating Trigger
			if(!task.getSchedule().equals(oldTask.getSchedule())) {
				Trigger newTrigger = this.getTrigger(task);
				this.scheduler.rescheduleJob(this.getTriggerKey(task), newTrigger);
			}
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER_TRIGGER);
		}
		
		// Copy writable properties
		oldTask.setConfig(task.getConfig());
		oldTask.setMarshalledConfig(this.getConfigString(task.getConfig()));
		oldTask.setSchedule(task.getSchedule());
		oldTask.setUpdated(new Date());
		
		retval += this.taskStatusDao.update(oldTask);
		
		return retval;
	}

	@Override
	public TaskStatus read(Long key) throws SQLException {
		TaskStatus retval = this.taskStatusDao.queryForId(key);
		if(retval == null) return null;
		
		retval.setConfig(this.getConfigMap(retval.getMarshalledConfig()));
		
		if(retval.getCurrentRun() != null) {
			this.taskRunStatusDao.refresh(retval.getCurrentRun());
		}
		
		if(retval.getLastRun() != null) {
			this.taskRunStatusDao.refresh(retval.getLastRun());
		}
		
		if(retval.getLastSuccessRun() != null) {
			this.taskRunStatusDao.refresh(retval.getLastSuccessRun());
		}
		
		if(retval.getNextRun() != null) {
			this.taskRunStatusDao.refresh(retval.getNextRun());
		}

		return retval;
	}
	
	public String getConfigString(Map<String, String> config) {
		if(config == null) return null;
		return SerializableUtils.serializeBase64(config);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getConfigMap(String config) {
		if(config == null) return null;
		return (Map<String,String>)SerializableUtils.deserializeBase64(config);
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
			throw new IllegalArgumentException(INVALID_CANONICAL_NAME, e);
		}
		
		JobDataMap dataMap = new JobDataMap(task.getConfig());
		JobDetail retval = newJob(clazz).withIdentity(this.getJobKey(task)).setJobData(dataMap).build();
		return retval;
	}
	
	private TriggerKey getTriggerKey(TaskStatus task) {
		TriggerKey retval = new TriggerKey(this.getTriggerName(task), QUARTZ_GROUP_NAME);
		return retval;
	}
	
	private String getTriggerName(TaskStatus task) {
		return String.format(TRIGGER_NAME_FORMAT, task.getName());
	}
	
	private JobKey getJobKey(TaskStatus task) {
		JobKey retval = new JobKey(task.getName(), QUARTZ_GROUP_NAME);
		return retval;
	}
}
