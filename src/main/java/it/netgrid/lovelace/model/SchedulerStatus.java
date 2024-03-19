package it.netgrid.lovelace.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="scheduler_status")
public class SchedulerStatus implements CrudObject<Long> {
	
	public static final String ID_FIELD_NAME = "sch_id";
	public static final String ACTIVE_FROM_FIELD_NAME = "sch_active_from";

	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=ACTIVE_FROM_FIELD_NAME)
	private Date activeFrom;
	
	@Transient
	private List<TaskStatus> tasks;
	
	@ForeignCollectionField
	private ForeignCollection<TaskStatus> tasksStatus;
	
	public SchedulerStatus() {
		this.tasks = new ArrayList<TaskStatus>();
	}

	@XmlElement(name="active_from")
	public Date getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	public List<TaskStatus> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskStatus> tasks) {
		this.tasks = tasks;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public ForeignCollection<TaskStatus> getTasksStatus() {
		return tasksStatus;
	}

	public void setTasksStatus(ForeignCollection<TaskStatus> tasksStatus) {
		this.tasksStatus = tasksStatus;
	}
	
}
