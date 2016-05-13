package it.netgrid.lovelace.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	public static final String LAST_RUN_ID_FIELD_NAME = "tst_last_trs_id";
	public static final String LAST_SUCCESS_RUN_ID_FIELD_NAME = "tst_last_success_trs_id";
	public static final String CURRENT_RUN_ID_FIELD_NAME = "tst_current_trs_id";
	
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
	
	@Transient
	private Date nextRunTime;
	
	@Column(name=LAST_RUN_ID_FIELD_NAME)
	private TaskRunStatus lastRun;
	
	@Column(name=CURRENT_RUN_ID_FIELD_NAME)
	private TaskRunStatus currentRun;
	
	@Column(name=LAST_SUCCESS_RUN_ID_FIELD_NAME)
	private TaskRunStatus lastSuccessRun;
	
	@ManyToOne
	@JoinColumn(name=SYSYEM_ID_FIELD_NAME)
	private SystemStatus systemStatus;
	
	@ForeignCollectionField
	private ForeignCollection<TaskRunStatus> taskRuns;
	
	@Transient
	private List<TaskRunStatus> runs;

	public TaskStatus() {
		this.runs = new ArrayList<TaskRunStatus>();
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
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(SystemStatus systemStatus) {
		this.systemStatus = systemStatus;
	}
	
	@XmlElement(name="next_run_time")
	public Date getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	@XmlElement(name="last_run")
	public TaskRunStatus getLastRun() {
		return lastRun;
	}

	public void setLastRun(TaskRunStatus lastRun) {
		this.lastRun = lastRun;
	}

	@XmlElement(name="current_run")
	public TaskRunStatus getCurrentRun() {
		return currentRun;
	}

	public void setCurrentRun(TaskRunStatus currentRun) {
		this.currentRun = currentRun;
	}

	@XmlElement(name="last_success_run")
	public TaskRunStatus getLastSuccessRun() {
		return lastSuccessRun;
	}

	public void setLastSuccessRun(TaskRunStatus lastSuccessRun) {
		this.lastSuccessRun = lastSuccessRun;
	}

	@XmlTransient
	public ForeignCollection<TaskRunStatus> getTaskRuns() {
		return taskRuns;
	}

	public void setTaskRuns(ForeignCollection<TaskRunStatus> taskRuns) {
		this.taskRuns = taskRuns;
	}

	public List<TaskRunStatus> getRuns() {
		return runs;
	}

	public void setRuns(List<TaskRunStatus> runs) {
		this.runs = runs;
	}

}
