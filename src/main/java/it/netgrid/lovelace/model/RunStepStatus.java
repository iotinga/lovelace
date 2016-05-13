package it.netgrid.lovelace.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="run_step_status")
public class RunStepStatus implements CrudObject<Long> {
	
	public static final String ID_FIELD_NAME = "rss_id";
	public static final String TASK_RUN_STATUS_ID_FIELD_NAME = "rss_trs_id";
	public static final String STEP_NAME_FIELD_NAME = "rss_name";
	public static final String STATUS_FIELD_NAME = "rss_status";
	public static final String START_TIME_FIELD_NAME = "rss_start_time";
	public static final String END_TIME_FIELD_NAME = "rss_end_time";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=START_TIME_FIELD_NAME)
	private Date startTime;
	
	@Column(name=END_TIME_FIELD_NAME)
	private Date endTime;
	
	@Column(name=STATUS_FIELD_NAME)
	private RunState status;
	
	@ManyToOne
	@JoinColumn(name=TASK_RUN_STATUS_ID_FIELD_NAME)
	private TaskRunStatus runStatus;
	
	@Column(name=STEP_NAME_FIELD_NAME)
	private String name;
	
	public RunStepStatus() {}

	@XmlElement(name="start_time")
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@XmlElement(name="end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
	public TaskRunStatus getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(TaskRunStatus runStatus) {
		this.runStatus = runStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
