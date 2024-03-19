package it.netgrid.lovelace.model;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.lovelace.Configuration;

public class DaoModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(DaoModule.class);

	@Override
	protected void configure() {
		//NOTHING TO DO
	}
	
	@Provides
	@Singleton
	public Dao<StepStatus, Long> getRunStepStatusDao(Configuration config, ConnectionSource connectionSource) throws SQLException {
		if( ! JdbcConnectionModule.areTableInitialized()) JdbcConnectionModule.initTables(connectionSource);
		return DaoManager.createDao(connectionSource, StepStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<SchedulerStatus, Long> getSystemStatusDao(ConnectionSource connectionSource) throws SQLException {
		if( ! JdbcConnectionModule.areTableInitialized()) JdbcConnectionModule.initTables(connectionSource);
		return DaoManager.createDao(connectionSource, SchedulerStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<RunStatus, Long> getTaskRunStatusDao(ConnectionSource connectionSource) throws SQLException {
		if( ! JdbcConnectionModule.areTableInitialized()) JdbcConnectionModule.initTables(connectionSource);
		return DaoManager.createDao(connectionSource, RunStatus.class);
	}
	
	@Provides
	@Singleton
	public Dao<TaskStatus, Long> getTaskStatusDao(ConnectionSource connectionSource) throws SQLException {
		if( ! JdbcConnectionModule.areTableInitialized()) JdbcConnectionModule.initTables(connectionSource);
		return DaoManager.createDao(connectionSource, TaskStatus.class);
	}

}
