package it.netgrid.lovelace.api;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.StepStatus;

public class StepStatusCrudService extends TemplateCrudService<StepStatus, Long> {

	private final Dao<StepStatus, Long> runStepStatusDao;
	
	@Inject
	public StepStatusCrudService(ConnectionSource connection, Dao<StepStatus, Long> runStepStatusDao) {
		super(connection);
		this.runStepStatusDao = runStepStatusDao;
	}

	@Override
	public int createRaw(StepStatus arg0) throws SQLException, IllegalArgumentException {
		Validate.notNull(arg0.getRunStatus());
		return this.runStepStatusDao.create(arg0);
	}

	@Override
	public int deleteRaw(StepStatus arg0) throws SQLException, IllegalArgumentException {
		return this.runStepStatusDao.delete(arg0);
	}

	@Override
	public int updateRaw(StepStatus arg0) throws SQLException, IllegalArgumentException {
		return this.runStepStatusDao.update(arg0);
	}

	@Override
	public StepStatus read(Long key) throws SQLException {
		return this.runStepStatusDao.queryForId(key);
	}

}
