package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class SchedulerStatusCrudService extends TemplateCrudService<SchedulerStatus, Long> {

	public static final String INVALID_REQUEST = "not-allowed";
	private final Dao<SchedulerStatus, Long> schedulerStatusDao;
	private final CrudService<TaskStatus, Long> taskStatusService;
	
	@Inject
	public SchedulerStatusCrudService(ConnectionSource connection, 
			Dao<SchedulerStatus, Long> schedulerStatusDao,
			CrudService<TaskStatus, Long> taskStatusService) {
		super(connection);
		this.schedulerStatusDao = schedulerStatusDao;
		this.taskStatusService = taskStatusService;
	}

	@Override
	public int createRaw(SchedulerStatus scheduler) throws SQLException, IllegalArgumentException {
		return this.schedulerStatusDao.create(scheduler);
	}

	@Override
	public int deleteRaw(SchedulerStatus scheduler) throws SQLException, IllegalArgumentException {
		throw new IllegalArgumentException(INVALID_REQUEST);
	}

	@Override
	public int updateRaw(SchedulerStatus scheduler) throws SQLException, IllegalArgumentException {
		return this.schedulerStatusDao.update(scheduler);
	}

	@Override
	public SchedulerStatus read(Long key) throws SQLException {
		SchedulerStatus retval = this.schedulerStatusDao.queryForId(key);
		
		if(retval == null) {
			this.create(this.buildSchedulerStatus());
			return this.read(key);
		}
		
		for(TaskStatus task : retval.getTasksStatus()) {
			TaskStatus item = this.taskStatusService.read(task.getId());
			retval.getTasks().add(item);
		}
		
		return retval;
	}

	private SchedulerStatus buildSchedulerStatus() {
		SchedulerStatus retval = new SchedulerStatus();
		retval.setActiveFrom(new Date());
		return retval;
	}
}
