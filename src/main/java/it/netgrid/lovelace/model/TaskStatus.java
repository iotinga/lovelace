package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="task_status")
public class TaskStatus implements CrudObject<Long> {

	public static final String ID_FIELD_NAME = "tst_id";
	public static final String SYSYEM_ID_FIELD_NAME = "tst_sys_id";
	public static final String CANONICAL_NAME_FIELD_NAME = "tst_canonical_name";
	public static final String NAME_FIELD_NAME = "tst_name";
	public static final String CREATION_FIELD_NAME = "tst_creation";
	public static final String UPDATED_FIELD_NAME = "tst_updated";
	public static final String CONFIG_FIELD_NAME = "tst_config";
	public static final String SCHEDULE_FIELD_NAME = "tst_schedule";
	public static final String NEXT_RUN_FIELD_NAME = "tst_next_run";
	public static final String LAST_RUN_FIELD_NAME = "tst_last_run";
	public static final String LAST_SUCCESS_RUN_FIELD_NAME = "tst_last_success_run";
	public static final String LAST_RUNNING_TIME_FIELD_NAME = "tst_last_running_time";
	public static final String CURRENT_START_TIME_FIELD_NAME = "tst_current_start_time";
	public static final String LAST_RESULT_FIELD_NAME = "tst_last_result";
	public static final String STATUS_FIELD_NAME = "tst_status";
	
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
	
	@Column(name=CONFIG_FIELD_NAME)
	private String marshalledConfig;
	
	@Column(name=SCHEDULE_FIELD_NAME)
	private String schedule;	
	
	@Column(name=NEXT_RUN_FIELD_NAME)
	private Date nextRun;
	
	@Column(name=LAST_RUN_FIELD_NAME)
	private Date lastRun;
	
	@Column(name=CURRENT_START_TIME_FIELD_NAME)
	private Date currentStartTime;
	
	@Column(name=LAST_SUCCESS_RUN_FIELD_NAME)
	private Date lastSuccessRun;
	
	@Column(name=LAST_RUNNING_TIME_FIELD_NAME)
	private BigDecimal lastRunningTime;
	
	@Transient
	private BigDecimal currentElapsedTime;
	
	@Column(name=LAST_RESULT_FIELD_NAME)
	private RunResult lastResult;
	
	@Column(name=STATUS_FIELD_NAME)
	private RunState status;
	
	@ManyToOne
	@JoinColumn(name=SYSYEM_ID_FIELD_NAME)
	private SystemStatus systemStatus;
	
	@ForeignCollectionField
	private ForeignCollection<TaskStepStatus> taskSteps;
	
	public TaskStatus() {}

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

	public Map<String, String> getConfig() {
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

	@XmlElement(name="next_run")
	public Date getNextRun() {
		return nextRun;
	}

	public void setNextRun(Date nextRun) {
		this.nextRun = nextRun;
	}

	@XmlElement(name="last_run")
	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	@XmlElement(name="last_success_run")
	public Date getLastSuccessRun() {
		return lastSuccessRun;
	}

	public void setLastSuccessRun(Date lastSuccessRun) {
		this.lastSuccessRun = lastSuccessRun;
	}

	@XmlElement(name="last_running_time")
	public BigDecimal getLastRunningTime() {
		return lastRunningTime;
	}

	public void setLastRunningTime(BigDecimal lastRunningTime) {
		this.lastRunningTime = lastRunningTime;
	}

	@XmlElement(name="current_elapsed_time")
	public BigDecimal getCurrentElapsedTime() {
		return currentElapsedTime;
	}

	public void setCurrentElapsedTime(BigDecimal currentElapsedTime) {
		this.currentElapsedTime = currentElapsedTime;
	}

	@XmlElement(name="last_result")
	public RunResult getLastResult() {
		return lastResult;
	}

	public void setLastResult(RunResult lastResult) {
		this.lastResult = lastResult;
	}

	public RunState getStatus() {
		return status;
	}

	public void setStatus(RunState status) {
		this.status = status;
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

	@XmlElement(name="current_start_time")
	public Date getCurrentStartTime() {
		return currentStartTime;
	}

	public void setCurrentStartTime(Date currentStartTime) {
		this.currentStartTime = currentStartTime;
	}

	@XmlTransient
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(SystemStatus systemStatus) {
		this.systemStatus = systemStatus;
	}

	@XmlTransient
	public ForeignCollection<TaskStepStatus> getTaskSteps() {
		return taskSteps;
	}

	public void setTaskSteps(ForeignCollection<TaskStepStatus> taskSteps) {
		this.taskSteps = taskSteps;
	}
	
}
