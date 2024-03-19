package it.netgrid.lovelace.model;

import java.util.Date;

import javax.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="step_status")
public class StepStatus implements CrudObject<Long> {
	
	public static final String ID_FIELD_NAME = "stp_id";
	public static final String RUN_STATUS_ID_FIELD_NAME = "stp_run_id";
	public static final String STEP_NAME_FIELD_NAME = "stp_name";
	public static final String EXECUTION_RESULT_FIELD_NAME = "stp_result";
	public static final String EXECUTION_STATE_FIELD_NAME = "stp_state";
	public static final String START_TIME_FIELD_NAME = "stp_start_time";
	public static final String END_TIME_FIELD_NAME = "stp_end_time";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=START_TIME_FIELD_NAME)
	private Date startTime;
	
	@Column(name=END_TIME_FIELD_NAME)
	private Date endTime;
	
	@Column(name=EXECUTION_STATE_FIELD_NAME)
	private ExecutionState state;
	
	@Column(name=EXECUTION_RESULT_FIELD_NAME)
	private ExecutionResult result;
	
	@OneToOne
	@JoinColumn(name=RUN_STATUS_ID_FIELD_NAME)
	private RunStatus runStatus;
	
	@Column(name=STEP_NAME_FIELD_NAME)
	private String name;
	
	public StepStatus() {}

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

	public ExecutionState getState() {
		return state;
	}

	public void setState(ExecutionState state) {
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public RunStatus getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(RunStatus runStatus) {
		this.runStatus = runStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExecutionResult getResult() {
		return result;
	}

	public void setResult(ExecutionResult result) {
		this.result = result;
	}
	
}
