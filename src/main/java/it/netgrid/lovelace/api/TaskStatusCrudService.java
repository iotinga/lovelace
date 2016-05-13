package it.netgrid.lovelace.api;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.Validate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;

import com.cronutils.validator.CronValidator;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.api.json.JSONUnmarshaller;

import it.netgrid.lovelace.model.TaskStatus;

public class TaskStatusCrudService extends TemplateCrudService<TaskStatus, Long> {

	public static final String INVALID_CANONICAL_NAME = "canonical_name";
	public static final String INVALID_SCHEDULER = "scheduler";
	public static final String QUARTZ_GROUP_NAME = "global";
	private static final String TRIGGER_NAME_FORMAT = "%s#TRIGGER";
	
	private final Scheduler scheduler;
	private final CronValidator cronValidator;
	private final Dao<TaskStatus, Long> taskStatusDao;
	private final JSONMarshaller marshaller;
	private final JSONUnmarshaller unmarshaller;
	
	@Inject
	public TaskStatusCrudService(ConnectionSource connection, 
			Scheduler scheduler, 
			CronValidator cronValidator,
			Dao<TaskStatus, Long> taskStatusDao,
			JSONMarshaller marshaller,
			JSONUnmarshaller unmarshaller) {
		super(connection);
		this.scheduler = scheduler;
		this.cronValidator = cronValidator;
		this.taskStatusDao = taskStatusDao;
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
	}

	@Override
	public int createRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		Validate.notBlank(task.getName());
		this.cronValidator.validate(task.getSchedule());
		
		task.setCreation(new Date());
		task.setMarshalledConfig(this.getConfigString(task.getConfig()));
		
		JobDetail detail = this.getJobDetail(task);
		Trigger trigger = this.getTrigger(task);
		
		try {
			this.scheduler.scheduleJob(detail, trigger);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(INVALID_SCHEDULER);
		}

		return 0;
	}

	@Override
	public int deleteRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRaw(TaskStatus task) throws SQLException, IllegalArgumentException {
		// avoid to update name!
		return 0;
	}

	@Override
	public TaskStatus read(Long key) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getConfigString(Map<String, String> config) {
		if(config == null) return null;
		StringWriter writer = new StringWriter();
		try {
			this.marshaller.marshallToJSON(config, writer);
		} catch (JAXBException e) {
			return null;
		}
		String retval = writer.toString();
		writer.flush();
		return retval;
	}
	
	public Map<String,String> getConfigMap(String jsonConfig) {
		if(jsonConfig == null) return null;
		StringReader reader = new StringReader(jsonConfig);
		try {
			@SuppressWarnings("unchecked")
			Map<String,String> retval = this.unmarshaller.unmarshalFromJSON(reader, Map.class);
			return retval;
		} catch (JAXBException e) {
			return null;
		}
	}

	private Trigger getTrigger(TaskStatus task) {
		Trigger retval = newTrigger()
				.withIdentity(this.getTriggerName(task), QUARTZ_GROUP_NAME)
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
	
	private String getTriggerName(TaskStatus task) {
		return String.format(TRIGGER_NAME_FORMAT, task.getName());
	}
	
	private JobKey getJobKey(TaskStatus task) {
		JobKey retval = new JobKey(task.getName(), QUARTZ_GROUP_NAME);
		return retval;
	}
}
