package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
public class TaskStepStatus implements CrudObject<Long> {
	
	public static final String ID_FIELD_NAME = "tss_id";
	public static final String LAST_RUN_FIELD_NAME = "tss_last_run";
	public static final String LAST_SUCCESS_RUN_FIELD_NAME = "tss_last_success_run";
	public static final String LAST_RUNNING_TIME_FIELD_NAME = "tss_last_running_time";
	public static final String CURRENT_ELAPSED_TIME_FIELD_NAME = "tss_current_elapsed_time";
	public static final String LAST_RESULT_FIELD_NAME = "tss_last_result";
	public static final String STATUS_FIELD_NAME = "tss_status";
	public static final String TASK_STATUS_ID_FIELD_NAME = "tss_tst_id";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=LAST_RUN_FIELD_NAME)
	private Date lastRun;
	
	@Column(name=LAST_SUCCESS_RUN_FIELD_NAME)
	private Date lastSuccessRun;
	
	@Column(name=LAST_RUNNING_TIME_FIELD_NAME)
	private BigDecimal lastRunningTime;
	
	@Column(name=CURRENT_ELAPSED_TIME_FIELD_NAME)
	private BigDecimal currentElapsedTime;
	
	@Column(name=LAST_RESULT_FIELD_NAME)
	private RunResult lastResult;
	
	@Column(name=STATUS_FIELD_NAME)
	private RunState status;
	
	@ManyToOne
	@JoinColumn(name=TASK_STATUS_ID_FIELD_NAME)
	private TaskStatus taskStatus;
	
	public TaskStepStatus() {}

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
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	
}
