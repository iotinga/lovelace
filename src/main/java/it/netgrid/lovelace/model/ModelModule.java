package it.netgrid.lovelace.model;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import it.netgrid.lovelace.Configuration;

public class ModelModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(ModelModule.class);

	@Override
	protected void configure() {
		//NOTHING TO DO
	}
	
	@Provides
	@Singleton
	public ConnectionSource getConnection(Configuration configuration) throws SQLException {
		JdbcPooledConnectionSource retval = new JdbcPooledConnectionSource(configuration.getJdbcConnectionUrl(), configuration.getJdbcUsername(), configuration.getJdbcPassword());
		try {
			TableUtils.createTableIfNotExists(retval, SchedulerStatus.class);
			TableUtils.createTableIfNotExists(retval, TaskStatus.class);
			TableUtils.createTableIfNotExists(retval, StepStatus.class);
			TableUtils.createTableIfNotExists(retval, RunStatus.class);
		} catch (SQLException e) {
			log.warn("SQL errors during DB creation");
			log.debug("SQL errors during DB creation:",e);
		}
		return retval;
	}
	
	@Provides
	@Singleton
	public Dao<StepStatus, Long> getRunStepStatusDao(ConnectionSource connectionSource) throws SQLException {
		return DaoManager.createDao(connectionSource, StepStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<SchedulerStatus, Long> getSystemStatusDao(ConnectionSource connectionSource) throws SQLException {
		return DaoManager.createDao(connectionSource, SchedulerStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<RunStatus, Long> getTaskRunStatusDao(ConnectionSource connectionSource) throws SQLException {
		return DaoManager.createDao(connectionSource, RunStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<TaskStatus, Long> getTaskStatusDao(ConnectionSource connectionSource) throws SQLException {
		return DaoManager.createDao(connectionSource, TaskStatus.class);
	}

}
