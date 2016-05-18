package it.netgrid.lovelace.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="task_run_status")
public class TaskRunStatus implements CrudObject<Long> {

	public static final String ID_FIELD_NAME = "trs_id";
	public static final String CREATION_DATE_FIELD_NAME = "trs_creation_date";
	public static final String START_DATE_FIELD_NAME = "trs_start_date";
	public static final String END_DATE_FIELD_NAME = "trs_end_date";
	public static final String RUN_STATE_FIELD_NAME = "trs_run_state";
	public static final String RUN_RESULT_FIELD_NAME = "trs_run_result";
	public static final String TASK_ID_FIELD_NAME = "trs_tst_id";
	public static final String CURRENT_RUN_STEP_ID_FIELD_NAME = "trs_tss_id";
	public static final String RUN_REASON_FIELD_NAME = "trs_run_reason";
	
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
	
	@ManyToOne
	@JoinColumn(name=TASK_ID_FIELD_NAME)
	private TaskStatus task;
	
	@ForeignCollectionField(orderColumnName=RunStepStatus.ID_FIELD_NAME)
	private ForeignCollection<RunStepStatus> runSteps;
	
	@OneToOne
	@JoinColumn(name=CURRENT_RUN_STEP_ID_FIELD_NAME)
	private RunStepStatus currentStep;
	
	@Transient
	private List<RunStepStatus> steps;
	
	public TaskRunStatus() {
		this.steps = new ArrayList<RunStepStatus>();
	}

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

	@XmlTransient
	public TaskStatus getTask() {
		return task;
	}

	public void setTask(TaskStatus task) {
		this.task = task;
	}

	@XmlElement(name="current_step")
	public RunStepStatus getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(RunStepStatus currentStep) {
		this.currentStep = currentStep;
	}

	public RunReason getReason() {
		return reason;
	}

	public void setReason(RunReason reason) {
		this.reason = reason;
	}

	@XmlTransient
	public ForeignCollection<RunStepStatus> getRunSteps() {
		return runSteps;
	}

	public void setRunSteps(ForeignCollection<RunStepStatus> runSteps) {
		this.runSteps = runSteps;
	}

	public List<RunStepStatus> getSteps() {
		return steps;
	}

	public void setSteps(List<RunStepStatus> steps) {
		this.steps = steps;
	}
	
}
