package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Date;

import com.google.inject.Inject;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.SystemStatus;

public class SystemStatusCrudService extends TemplateCrudService<SystemStatus, Long> {

	@Inject
	public SystemStatusCrudService(ConnectionSource connection) {
		super(connection);
	}

	@Override
	public int createRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRaw(SystemStatus arg0) throws SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SystemStatus read(Long key) throws SQLException {
		SystemStatus retval = new SystemStatus();
		retval.setActiveFrom(new Date());
		return retval;
	}

}
