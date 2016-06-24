package it.netgrid.lovelace.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import it.netgrid.commons.SerializableUtils;
import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="task_status")
public class TaskStatus implements CrudObject<Long> {

	public static final String ID_FIELD_NAME = "tsk_id";
	public static final String SCHEDULER_STATUS_ID_FIELD_NAME = "tsk_sys_id";
	public static final String CANONICAL_NAME_FIELD_NAME = "tsk_canonical_name";
	public static final String NAME_FIELD_NAME = "tsk_name";
	public static final String CREATION_FIELD_NAME = "tsk_creation";
	public static final String UPDATED_FIELD_NAME = "tsk_updated";
	public static final String MARSHALLED_CONFIG_FIELD_NAME = "tsk_config";
	public static final String SCHEDULE_FIELD_NAME = "tsk_schedule";
	public static final String LAST_RUN_ID_FIELD_NAME = "tsk_last_trs_id";
	public static final String LAST_SUCCESS_RUN_ID_FIELD_NAME = "tsk_last_success_trs_id";
	public static final String CURRENT_RUN_ID_FIELD_NAME = "tsk_current_trs_id";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=CANONICAL_NAME_FIELD_NAME)
	private String canonicalName;
	
	@Column(name=NAME_FIELD_NAME)
	private String name;
	
	@Column(name=CREATION_FIELD_NAME)
	private Date creation;
	
	@Column(name=UPDATED_FIELD_NAME)
	private Date updated;
	
	@Transient
	private Map<String, String> config;
	
	@Column(name=MARSHALLED_CONFIG_FIELD_NAME)
	private String marshalledConfig;
	
	@Column(name=SCHEDULE_FIELD_NAME)
	private String schedule;	
	
	@OneToOne
	@JoinColumn(name=LAST_RUN_ID_FIELD_NAME)
	private RunStatus lastRun;

	@OneToOne
	@JoinColumn(name=CURRENT_RUN_ID_FIELD_NAME)
	private RunStatus currentRun;

	@OneToOne
	@JoinColumn(name=LAST_SUCCESS_RUN_ID_FIELD_NAME)
	private RunStatus lastSuccessRun;
	
	@OneToOne
	@JoinColumn(name=SCHEDULER_STATUS_ID_FIELD_NAME)
	private SchedulerStatus schedulerStatus;
	
	@ForeignCollectionField
	private ForeignCollection<RunStatus> taskRuns;
	
	@Transient
	private Date nextRunTime;

	public TaskStatus() {
	}

	@XmlElement(name="canonical_name")
	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getConfig() {
		if(this.config == null && this.marshalledConfig != null) {
			this.config = (Map<String, String>) SerializableUtils.deserializeBase64(this.marshalledConfig);
		}
		if(this.config == null) {
			this.config = new HashMap<String, String>();
		}
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public String getMarshalledConfig() {
		return marshalledConfig;
	}

	public void setMarshalledConfig(String marshalledConfig) {
		this.marshalledConfig = marshalledConfig;
	}
	
	@XmlTransient
	public SchedulerStatus getSchedulerStatus() {
		return schedulerStatus;
	}

	public void setSchedulerStatus(SchedulerStatus schedulerStatus) {
		this.schedulerStatus = schedulerStatus;
	}
	
	@XmlElement(name="next_run_time")
	public Date getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	@XmlElement(name="last_run")
	public RunStatus getLastRun() {
		return lastRun;
	}

	public void setLastRun(RunStatus lastRun) {
		this.lastRun = lastRun;
	}

	@XmlElement(name="current_run")
	public RunStatus getCurrentRun() {
		return currentRun;
	}

	public void setCurrentRun(RunStatus currentRun) {
		this.currentRun = currentRun;
	}

	@XmlElement(name="last_success_run")
	public RunStatus getLastSuccessRun() {
		return lastSuccessRun;
	}

	public void setLastSuccessRun(RunStatus lastSuccessRun) {
		this.lastSuccessRun = lastSuccessRun;
	}

	@XmlTransient
	public ForeignCollection<RunStatus> getTaskRuns() {
		return taskRuns;
	}

	public void setTaskRuns(ForeignCollection<RunStatus> taskRuns) {
		this.taskRuns = taskRuns;
	}
}
