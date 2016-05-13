package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import it.netgrid.commons.data.CrudObject;

@XmlRootElement
@Entity(name="system_status")
public class SystemStatus implements CrudObject<Long> {
	
	public static final String ID_FIELD_NAME = "sys_id";
	public static final String UPTIME_FIELD_NAME = "sys_uptime";
	public static final String ACTIVE_FROM_FIELD_NAME = "sys_active_from";

	@Id
	@GeneratedValue
	@Column(name=ID_FIELD_NAME)
	private Long id;
	
	@Column(name=UPTIME_FIELD_NAME)
	private BigDecimal uptime;
	
	@Column(name=ACTIVE_FROM_FIELD_NAME)
	private Date activeFrom;
	
	@Transient
	private List<TaskStatus> tasks;
	
	@ForeignCollectionField
	private ForeignCollection<TaskStatus> tasksStatus;
	
	public SystemStatus() {
		this.tasks = new ArrayList<TaskStatus>();
	}

	public BigDecimal getUptime() {
		return uptime;
	}

	public void setUptime(BigDecimal uptime) {
		this.uptime = uptime;
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
