package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.TaskRunStatus;

public class TaskRunStatusCrudService extends TemplateCrudService<TaskRunStatus, Long> {

	public static final String INVALID_TASK = "task";
	private final Dao<TaskRunStatus, Long> taskRunStatusDao;
	
	@Inject
	public TaskRunStatusCrudService(ConnectionSource connection, Dao<TaskRunStatus, Long> taskRunStatusDao) {
		super(connection);
		this.taskRunStatusDao = taskRunStatusDao;
	}

	@Override
	public int createRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		Validate.notNull(arg0.getTask(), INVALID_TASK);
		arg0.setCreationDate(new Date());
		return this.taskRunStatusDao.create(arg0);
	}

	@Override
	public int deleteRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		return this.taskRunStatusDao.delete(arg0);
	}

	@Override
	public int updateRaw(TaskRunStatus arg0) throws SQLException, IllegalArgumentException {
		return this.taskRunStatusDao.update(arg0);
	}

	@Override
	public TaskRunStatus read(Long key) throws SQLException {
		TaskRunStatus retval = this.taskRunStatusDao.queryForId(key);
		for(RunStepStatus step : retval.getRunSteps()) {
			retval.getSteps().add(step);
		}
		return retval;
	}

}
