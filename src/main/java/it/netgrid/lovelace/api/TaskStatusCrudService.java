package it.netgrid.lovelace.api;

import java.sql.SQLException;

import com.google.inject.Inject;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.TaskStatus;

public class TaskStatusCrudService extends TemplateCrudService<TaskStatus, Long> {

	@Inject
	public TaskStatusCrudService(ConnectionSource connection) {
		super(connection);
	}

	@Override
	public int createRaw(TaskStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteRaw(TaskStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRaw(TaskStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TaskStatus read(Long key) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
