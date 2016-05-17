package it.netgrid.lovelace.api;

import java.sql.SQLException;

import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskStatus;

public interface RunStatusService {
	public RunStepStatus start(TaskStatus task, String firstStepName) throws SQLException;
	public RunStepStatus nextStep(TaskStatus task, RunResult currentStepResult, String nextStepName) throws SQLException;
	public RunStepStatus end(TaskStatus task, RunResult stepResult, RunResult result) throws SQLException;
}
