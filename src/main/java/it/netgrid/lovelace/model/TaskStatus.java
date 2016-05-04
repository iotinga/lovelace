package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskStatus {

	private String canonicalName;
	private String name;
	private Date creation;
	private Date updated;
	private Map<String, Object> config;
	private String schedule;	
	private Date nextRun;
	private Date lastRun;
	private Date lastSuccessRun;
	private BigDecimal lastRunningTime;
	private BigDecimal currentElapsedTime;
	private RunResult lastResult;
	private RunState status;
	
	public TaskStatus() {}

	@XmlElement(name="canonical_name")
	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Date getNextRun() {
		return nextRun;
	}

	public void setNextRun(Date nextRun) {
		this.nextRun = nextRun;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public Date getLastSuccessRun() {
		return lastSuccessRun;
	}

	public void setLastSuccessRun(Date lastSuccessRun) {
		this.lastSuccessRun = lastSuccessRun;
	}

	public BigDecimal getLastRunningTime() {
		return lastRunningTime;
	}

	public void setLastRunningTime(BigDecimal lastRunningTime) {
		this.lastRunningTime = lastRunningTime;
	}

	public BigDecimal getCurrentElapsedTime() {
		return currentElapsedTime;
	}

	public void setCurrentElapsedTime(BigDecimal currentElapsedTime) {
		this.currentElapsedTime = currentElapsedTime;
	}

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
	
}
