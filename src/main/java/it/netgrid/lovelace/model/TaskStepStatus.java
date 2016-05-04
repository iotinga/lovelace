package it.netgrid.lovelace.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskStepStatus {
	
	private Date lastRun;
	private Date lastSuccessRun;
	private BigDecimal lastRunningTime;
	private BigDecimal currentElapsedTime;
	private RunResult lastResult;
	private RunState status;
	
	public TaskStepStatus() {}

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
