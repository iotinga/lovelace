package it.netgrid.lovelace.api;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.SystemStatus;
import it.netgrid.lovelace.model.TaskStatus;

public class SystemStatusCrudService extends TemplateCrudService<SystemStatus, Long> {

	public static final String INVALID_REQUEST = "not-allowed";
	private final Dao<SystemStatus, Long> systemStatusDao;
	private final CrudService<TaskStatus, Long> taskStatusService;
	
	@Inject
	public SystemStatusCrudService(ConnectionSource connection, 
			Dao<SystemStatus, Long> systemStatusDao,
			CrudService<TaskStatus, Long> taskStatusService) {
		super(connection);
		this.systemStatusDao = systemStatusDao;
		this.taskStatusService = taskStatusService;
	}

	@Override
	public int createRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		return this.systemStatusDao.create(arg0);
	}

	@Override
	public int deleteRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		throw new IllegalArgumentException(INVALID_REQUEST);
	}

	@Override
	public int updateRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		return this.systemStatusDao.update(arg0);
	}

	@Override
	public SystemStatus read(Long key) throws SQLException {
		SystemStatus retval = this.systemStatusDao.queryForId(key);
		
		if(retval == null) {
			this.create(this.buildSystemStatus());
			return this.read(key);
		}
		
		for(TaskStatus task : retval.getTasksStatus()) {
			TaskStatus item = this.taskStatusService.read(task.getId());
			retval.getTasks().add(item);
		}
		
		return retval;
	}

	private SystemStatus buildSystemStatus() {
		SystemStatus retval = new SystemStatus();
		retval.setActiveFrom(new Date());
		retval.setUptime(BigDecimal.ZERO);
		return retval;
	}
}
