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
@Entity(name="run_status")
public class RunStatus implements CrudObject<Long> {

	public static final String ID_FIELD_NAME = "run_id";
	public static final String CREATION_DATE_FIELD_NAME = "run_creation_date";
	public static final String START_DATE_FIELD_NAME = "run_start_date";
	public static final String END_DATE_FIELD_NAME = "run_end_date";
	public static final String EXECUTION_STATE_FIELD_NAME = "run_execution_state";
	public static final String EXECUTION_RESULT_FIELD_NAME = "run_execution_result";
	public static final String TOTAL_STEPS_COUNT_FIELD_NAME = "run_total_steps_count";
	public static final String TASK_STATUS_ID_FIELD_NAME = "run_tsk_id";
	public static final String CURRENT_STEP_ID_FIELD_NAME = "run_stp_id";
	
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
	
	@Column(name=EXECUTION_STATE_FIELD_NAME)
	private ExecutionState state;
	
	@Column(name=EXECUTION_RESULT_FIELD_NAME)
	private ExecutionResult result;
	
	@ManyToOne
	@JoinColumn(name=TASK_STATUS_ID_FIELD_NAME)
	private TaskStatus taskStatus;
	
	@ForeignCollectionField(orderColumnName=StepStatus.ID_FIELD_NAME)
	private ForeignCollection<StepStatus> stepsStatus;
	
	@OneToOne
	@JoinColumn(name=CURRENT_STEP_ID_FIELD_NAME)
	private StepStatus currentStep;
	
	@Transient
	private List<StepStatus> steps;
	
	@Column(name=TOTAL_STEPS_COUNT_FIELD_NAME)
	private int totalStepsCount;
	
	public RunStatus() {
		this.steps = new ArrayList<StepStatus>();
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

	public ExecutionState getState() {
		return state;
	}

	public void setState(ExecutionState state) {
		this.state = state;
	}

	public ExecutionResult getResult() {
		return result;
	}

	public void setResult(ExecutionResult result) {
		this.result = result;
	}

	@XmlTransient
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	@XmlElement(name="current_step")
	public StepStatus getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(StepStatus currentStep) {
		this.currentStep = currentStep;
	}

	@XmlTransient
	public ForeignCollection<StepStatus> getStepsStatus() {
		return stepsStatus;
	}

	public void setStepsStatus(ForeignCollection<StepStatus> stepsStatus) {
		this.stepsStatus = stepsStatus;
	}

	public List<StepStatus> getSteps() {
		return steps;
	}

	public void setSteps(List<StepStatus> steps) {
		this.steps = steps;
	}

	@XmlElement(name="total_steps_count")
	public int getTotalStepsCount() {
		return totalStepsCount;
	}

	public void setTotalStepsCount(int totalStepsCount) {
		this.totalStepsCount = totalStepsCount;
	}
	
}
