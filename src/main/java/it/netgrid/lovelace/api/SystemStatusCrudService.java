package it.netgrid.lovelace.api;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.model.SystemStatus;

public class SystemStatusCrudService extends TemplateCrudService<SystemStatus, Long> {

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
		// TODO Auto-generated method stub
		return null;
	}

}
