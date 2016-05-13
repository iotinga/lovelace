package it.netgrid.lovelace.api;

import java.sql.SQLException;

import com.google.inject.Inject;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.TaskRunStatus;

public class TaskRunStatusCrudService extends TemplateCrudService<TaskRunStatus, Long> {

	@Inject
	public TaskRunStatusCrudService(ConnectionSource connection) {
		super(connection);
	}

	@Override
	public int createRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TaskRunStatus read(Long key) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
