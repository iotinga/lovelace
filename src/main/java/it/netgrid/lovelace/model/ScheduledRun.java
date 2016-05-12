package it.netgrid.lovelace.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity(name="scheduled_run")
public class ScheduledRun {

	public static final String ID_FIELD_NAME = "scr_id";
	public static final String CREATION_DATE_FIELD_NAME = "scr_creation_date";
	public static final String START_DATE_FIELD_NAME = "scr_start_date";
	public static final String END_DATE_FIELD_NAME = "scr_end_date";
	public static final String RUN_STATE_FIELD_NAME = "scr_run_state";
	public static final String RUN_RESULT_FIELD_NAME = "scr_run_result";
	public static final String TASK_ID_FIELD_NAME = "scr_tst_id";
	public static final String TASK_STEP_STATUS_ID_FIELD_NAME = "scr_tss_id";
	public static final String RUN_REASON_FIELD_NAME = "scr_run_reason";
	
	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	@Column(name=CREATION_DATE_FIELD_NAME)
	private Date creationDate;
	@Column(name=START_DATE_FIELD_NAME)
	private Date startDate;
	@Column(name=END_DATE_FIELD_NAME)
	private Date endDate;
	@Column(name=RUN_STATE_FIELD_NAME)
	private RunState state;
	@Column(name=RUN_RESULT_FIELD_NAME)
	private RunResult result;
	@Column(name=RUN_REASON_FIELD_NAME)
	private RunReason reason;
	
	@JoinColumn(name=TASK_ID_FIELD_NAME)
	private TaskStatus task;
	
	@JoinColumn(name=TASK_STEP_STATUS_ID_FIELD_NAME)
	private TaskStepStatus currentStep;
	
	public ScheduledRun() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@XmlElement(name="start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@XmlElement(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public RunState getState() {
		return state;
	}

	public void setState(RunState state) {
		this.state = state;
	}

	public RunResult getResult() {
		return result;
	}

	public void setResult(RunResult result) {
		this.result = result;
	}

	public TaskStatus getTask() {
		return task;
	}

	public void setTask(TaskStatus task) {
		this.task = task;
	}

	@XmlElement(name="current_step")
	public TaskStepStatus getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(TaskStepStatus currentStep) {
		this.currentStep = currentStep;
	}

	public RunReason getReason() {
		return reason;
	}

	public void setReason(RunReason reason) {
		this.reason = reason;
	}
}
