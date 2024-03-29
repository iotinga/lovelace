package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import jakarta.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.commons.ormlite.TemplateCrudService;
import it.netgrid.lovelace.model.RunStatus;

public class RunStatusCrudService extends TemplateCrudService<RunStatus, Long> {

	public static final String INVALID_TASK = "task";
	private final Dao<RunStatus, Long> runStatusDao;
	private final Dao<StepStatus, Long> stepStatusDao;
	
	@Inject
	public RunStatusCrudService(ConnectionSource connection, 
			Dao<RunStatus, Long> runStatusDao,
			Dao<StepStatus, Long> stepStatusDao) {
		super(connection);
		this.runStatusDao = runStatusDao;
		this.stepStatusDao = stepStatusDao;
	}

	@Override
	public int createRaw(RunStatus run) throws SQLException, IllegalArgumentException {
		Validate.notNull(run.getTaskStatus(), INVALID_TASK);
		run.setCreationDate(new Date());
		return this.runStatusDao.create(run);
	}

	@Override
	public int deleteRaw(RunStatus run) throws SQLException, IllegalArgumentException {
		return this.runStatusDao.delete(run);
	}

	@Override
	public int updateRaw(RunStatus run) throws SQLException, IllegalArgumentException {
		return this.runStatusDao.update(run);
	}

	@Override
	public RunStatus read(Long key) throws SQLException {
		RunStatus retval = this.runStatusDao.queryForId(key);
		for(StepStatus step : retval.getStepsStatus()) {
			stepStatusDao.refresh(step);
			retval.getSteps().add(step);
		}
		return retval;
	}

}
