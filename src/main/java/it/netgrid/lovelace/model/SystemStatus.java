package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SystemStatus {

	private BigDecimal uptime;
	private Date activeFrom;
	private List<TaskStatus> tasks;
	
	public SystemStatus() {}

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
	
}
