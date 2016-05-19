package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

import com.cronutils.validator.CronValidator;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.SerializableUtils;
import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.quartz.SchedulerUtils;

public class TaskStatusCrudService extends TemplateCrudService<TaskStatus, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(TaskStatusCrudService.class);

	public static final String INVALID_NAME = "name";
	public static final String INVALID_CANONICAL_NAME = "canonical_name";
	public static final String INVALID_SCHEDULER = "scheduler";
	public static final String INVALID_SCHEDULER_JOB_DETAILS = "scheduler/job_details";
	public static final String INVALID_SCHEDULER_TRIGGER = "scheduler/trigger";
	
	private final Configuration config;
	private final Scheduler scheduler;
	private final SchedulerUtils schedulerUtils;
	private final CronValidator cronValidator;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final Dao<RunStatus, Long> taskRunStatusDao;
	private final CrudService<SchedulerStatus, Long> systemStatusService;
	private final CrudService<RunStatus, Long> taskRunStatusCrudService;
	
	@Inject
	public TaskStatusCrudService(ConnectionSource connection, 
			SchedulerUtils schedulerUtils,
			Configuration config,
			Scheduler scheduler, 
			CronValidator cronValidator,
			Dao<TaskStatus, Long> taskStatusDao,
			Dao<RunStatus, Long> taskRunStatusDao,
			CrudService<RunStatus, Long> taskRunStatusCrudService,
			CrudService<SchedulerStatus, Long> systemStatusService) {
		super(connection);
		this.config = config;
		this.scheduler = scheduler;
		this.schedulerUtils = schedulerUtils;
		this.cronValidator = cronValidator;
		this.taskStatusDao = taskStatusDao;
		this.taskRunStatusDao = taskRunStatusDao;
		this.taskRunStatusCrudService = taskRunStatusCrudService;
		this.systemStatusService = systemStatusService;
	}

	@Override
	public int createRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		Validate.notBlank(task.getName());
		this.cronValidator.validate(task.getSchedule());
		
		SchedulerStatus system = this.systemStatusService.read(this.config.getSystemId());
		task.setSchedulerStatus(system);
		task.setCreation(new Date());
		task.setMarshalledConfig(this.getConfigString(task.getConfig()));
		task.setUpdated(task.getCreation());
		
		JobDetail detail = this.createJobDetail(task);
		Trigger trigger = this.getTrigger(task);
		
		int retval = this.taskStatusDao.create(task);

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
		for(RunStatus run : task.getTaskRuns()) {
			retval += this.taskRunStatusCrudService.deleteRaw(run);
		}
		
		retval += this.taskStatusDao.delete(task);

		// Scheduler operations have to been treated as "COMMIT"s:
		// Scheduler can rollback DB operations, 
		// DB will not rollback scheduler operations
		try {
			this.scheduler.deleteJob(this.schedulerUtils.getJobKey(task));
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

		this.cronValidator.validate(task.getSchedule());
		
		int retval = 0;
		try {
			// Updating Job Details
			JobDetail jobDetail = this.scheduler.getJobDetail(this.schedulerUtils.getJobKey(task));
			jobDetail.getJobDataMap().clear();
			jobDetail.getJobDataMap().putAll(task.getConfig());
			Set<Trigger> triggers = new HashSet<Trigger>(this.scheduler.getTriggersOfJob(this.schedulerUtils.getJobKey(task)));
			this.scheduler.scheduleJob(jobDetail, triggers, true);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER_JOB_DETAILS);
		}

		try {
			// Updating Trigger
			if(!task.getSchedule().equals(oldTask.getSchedule())) {
				Trigger newTrigger = this.getTrigger(task);
				this.scheduler.rescheduleJob(this.schedulerUtils.getTriggerKey(task), newTrigger);
			}
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER_TRIGGER);
		}
		
		// Copy writable properties
		oldTask.setConfig(task.getConfig());
		oldTask.setMarshalledConfig(this.getConfigString(task.getConfig()));
		oldTask.setSchedule(task.getSchedule());
		oldTask.setUpdated(new Date());
		oldTask.setCurrentRun(task.getCurrentRun());
		oldTask.setLastRun(task.getLastRun());
		oldTask.setLastSuccessRun(task.getLastSuccessRun());
		
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
		
		try {
			Trigger trigger = this.scheduler.getTrigger(this.schedulerUtils.getTriggerKey(retval));
			retval.setNextRunTime(trigger.getNextFireTime());
		} catch (SchedulerException e) {
			log.warn("Unable to read trigger reference for task: " + retval.getName());
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
			.withIdentity(this.schedulerUtils.getTriggerKey(task))
			.startNow()
			.withSchedule(cronSchedule(task.getSchedule()))
			.forJob(this.schedulerUtils.getJobKey(task))
			.build();
		return retval;
	}
	
	@SuppressWarnings("unchecked")
	private JobDetail createJobDetail(TaskStatus task) {
		Class<? extends Job> clazz = null;
		try {
			clazz = (Class<? extends Job>) Class.forName(task.getCanonicalName());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(INVALID_CANONICAL_NAME, e);
		}
		
		JobDataMap dataMap = new JobDataMap(task.getConfig());
		JobDetail retval = newJob(clazz).withIdentity(this.schedulerUtils.getJobKey(task)).setJobData(dataMap).build();
		return retval;
	}
}
