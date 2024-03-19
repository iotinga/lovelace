package it.netgrid.lovelace.model;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import it.netgrid.lovelace.Configuration;

public class JdbcConnectionModule extends AbstractModule {
	
	private static final Logger log = LoggerFactory.getLogger(JdbcConnectionModule.class);
	
	private static boolean tablesInitialized = false;

	@Override
	protected void configure() { }
	

	
	@Provides
	@Singleton
	public ConnectionSource getConnection(Configuration configuration) throws SQLException {
		JdbcPooledConnectionSource retval = new JdbcPooledConnectionSource(configuration.getJdbcConnectionUrl(), configuration.getJdbcUsername(), configuration.getJdbcPassword());
		initTables(retval);
		return retval;
	}
	
	public static final void initTables(ConnectionSource connection) {
		
		tablesInitialized = true;
		
		try {
			TableUtils.createTableIfNotExists(connection, SchedulerStatus.class);
			TableUtils.createTableIfNotExists(connection, TaskStatus.class);
			TableUtils.createTableIfNotExists(connection, StepStatus.class);
			TableUtils.createTableIfNotExists(connection, RunStatus.class);
		} catch (SQLException e) {
			log.warn("SQL errors during DB creation");
			log.debug("SQL errors during DB creation:",e);
		}
	} 
	
	public static final boolean areTableInitialized() {
		return tablesInitialized;
	}

}
