package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.Map;

import jakarta.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.ormlite.TemplateBulkService;
import it.netgrid.lovelace.model.TaskStatus;

public class TaskStatusBulkService extends TemplateBulkService<TaskStatus, Long> {
	

	private final Dao<TaskStatus, Long> taskStatusDao;

	@Inject
	public  TaskStatusBulkService(ConnectionSource connection,
			Dao<TaskStatus, Long> taskStatusDao) {
		super(connection);
		this.taskStatusDao = taskStatusDao;
	}

	@Override
	public void applyFilter(QueryBuilder<?, Long> query, Map<String, Object> filter) throws SQLException {
		boolean first = true;

		if (filter.containsKey(TaskStatus.NAME_FIELD_NAME)) {
			if (!first) {
				query.where().and().like(TaskStatus.NAME_FIELD_NAME, filter.get(TaskStatus.NAME_FIELD_NAME));
			} else {
				query.where().like(TaskStatus.NAME_FIELD_NAME, filter.get(TaskStatus.NAME_FIELD_NAME));
			}
			first = false;
		}
	}

	@Override
	public QueryBuilder<?, Long> initQueryBuilder() {
		QueryBuilder<TaskStatus, Long> query = this.taskStatusDao.queryBuilder();
		return query;
	}

}
