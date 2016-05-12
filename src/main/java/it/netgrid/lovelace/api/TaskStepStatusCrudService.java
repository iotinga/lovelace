package it.netgrid.lovelace.api;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.TaskStepStatus;

public class TaskStepStatusCrudService extends TemplateCrudService<TaskStepStatus, Long> {

	public TaskStepStatusCrudService(ConnectionSource connection) {
		super(connection);
	}

	@Override
	public int createRaw(TaskStepStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteRaw(TaskStepStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRaw(TaskStepStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TaskStepStatus read(Long key) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
