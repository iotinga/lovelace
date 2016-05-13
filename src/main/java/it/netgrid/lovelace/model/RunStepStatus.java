package it.netgrid.lovelace.model;

import java.math.BigDecimal;

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
	public static final String CURRENT_ELAPSED_TIME_FIELD_NAME = "rss_current_elapsed_time";
	public static final String STATUS_FIELD_NAME = "rss_status";
	public static final String TASK_STATUS_ID_FIELD_NAME = "rss_tst_id";
	public static final String TASK_RUN_STATUS_ID_FIELD_NAME = "rss_trs_id";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=CURRENT_ELAPSED_TIME_FIELD_NAME)
	private BigDecimal currentElapsedTime;
	
	@Column(name=STATUS_FIELD_NAME)
	private RunState status;
	
	@ManyToOne
	@JoinColumn(name=TASK_STATUS_ID_FIELD_NAME)
	private TaskStatus taskStatus;
	
	@ManyToOne
	@JoinColumn(name=TASK_RUN_STATUS_ID_FIELD_NAME)
	private TaskRunStatus runStatus;
	
	public RunStepStatus() {}

	@XmlElement(name="current_elapsed_time")
	public BigDecimal getCurrentElapsedTime() {
		return currentElapsedTime;
	}

	public void setCurrentElapsedTime(BigDecimal currentElapsedTime) {
		this.currentElapsedTime = currentElapsedTime;
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
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	
}
