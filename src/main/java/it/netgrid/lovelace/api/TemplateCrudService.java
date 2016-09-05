package it.netgrid.lovelace.api;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import it.netgrid.commons.data.CrudObject;
import it.netgrid.commons.data.CrudService;

public abstract class TemplateCrudService<T extends CrudObject<ID>, ID> implements CrudService<T, ID> {
	
	protected final ConnectionSource connection;
	
	protected TemplateCrudService(ConnectionSource connection) {
		this.connection = connection;
	}

	@Override
	public T create(final T object) throws SQLException {
		Integer affected = TransactionManager.callInTransaction(connection, new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return createRaw(object);
			}
			
		});
		
		return (affected > 0) ? this.read(object.getId()) : object;
	}

	@Override
	public abstract T read(ID key) throws SQLException;

	@Override
	public T update(final T object) throws SQLException {
		Integer affected = TransactionManager.callInTransaction(connection, new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return updateRaw(object);
			}
			
		});

		return (affected > 0) ? this.read(object.getId()) : object;	
	}

	@Override
	public T delete(final T object) throws SQLException {
		TransactionManager.callInTransaction(connection, new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return deleteRaw(object);
			}
			
		});
		
		return object;		
	}
	
}
