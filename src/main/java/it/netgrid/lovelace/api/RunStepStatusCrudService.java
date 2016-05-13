package it.netgrid.lovelace.api;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.RunStepStatus;

public class RunStepStatusCrudService extends TemplateCrudService<RunStepStatus, Long> {

	private final Dao<RunStepStatus, Long> runStepStatusDao;
	
	@Inject
	public RunStepStatusCrudService(ConnectionSource connection, Dao<RunStepStatus, Long> runStepStatusDao) {
		super(connection);
		this.runStepStatusDao = runStepStatusDao;
	}

	@Override
	public int createRaw(RunStepStatus arg0) throws SQLException, IllegalArgumentException {
		Validate.notNull(arg0.getRunStatus());
		return this.runStepStatusDao.create(arg0);
	}

	@Override
	public int deleteRaw(RunStepStatus arg0) throws SQLException, IllegalArgumentException {
		return this.runStepStatusDao.delete(arg0);
	}

	@Override
	public int updateRaw(RunStepStatus arg0) throws SQLException, IllegalArgumentException {
		return this.runStepStatusDao.update(arg0);
	}

	@Override
	public RunStepStatus read(Long key) throws SQLException {
		return this.runStepStatusDao.queryForId(key);
	}

}
