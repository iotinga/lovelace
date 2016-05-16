package it.netgrid.lovelace.api;

import java.sql.SQLException;

import it.netgrid.lovelace.model.RunResult;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskStatus;

public interface RunStatusService {
	public RunStepStatus start(final TaskStatus task, final String firstStepName) throws SQLException;
	public RunStepStatus nextStep(TaskStatus task, RunResult currentStepResult, String nextStepName) throws SQLException;
	public RunStepStatus end(final TaskStatus task, final RunResult result) throws SQLException;
}
