package it.netgrid.lovelace.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
	
	@OneToMany
	@JoinColumn
	private Collection<TaskStatus> tasksStatus;
	
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
	public Collection<TaskStatus> getTasksStatus() {
		return tasksStatus;
	}

	public void setTasksStatus(Collection<TaskStatus> tasksStatus) {
		this.tasksStatus = tasksStatus;
	}
	
}
