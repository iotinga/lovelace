package it.netgrid.lovelace.api;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;

import jakarta.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.ormlite.TemplateCrudService;
import it.netgrid.lovelace.model.StepStatus;

public class StepStatusCrudService extends TemplateCrudService<StepStatus, Long> {

	private final Dao<StepStatus, Long> stepStatusDao;
	
	@Inject
	public StepStatusCrudService(ConnectionSource connection, Dao<StepStatus, Long> stepStatusDao) {
		super(connection);
		this.stepStatusDao = stepStatusDao;
	}

	@Override
	public int createRaw(StepStatus step) throws SQLException, IllegalArgumentException {
		Validate.notNull(step.getRunStatus());
		return this.stepStatusDao.create(step);
	}

	@Override
	public int deleteRaw(StepStatus step) throws SQLException, IllegalArgumentException {
		return this.stepStatusDao.delete(step);
	}

	@Override
	public int updateRaw(StepStatus step) throws SQLException, IllegalArgumentException {
		return this.stepStatusDao.update(step);
	}

	@Override
	public StepStatus read(Long key) throws SQLException {
		return this.stepStatusDao.queryForId(key);
	}

}
