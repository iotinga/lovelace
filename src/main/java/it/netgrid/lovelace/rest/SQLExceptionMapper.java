package it.netgrid.lovelace.rest;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;

import it.netgrid.lovelace.model.ErrorMessage;

@Provider
@Singleton
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

	@Override
	public Response toResponse(SQLException exception) {
		Throwable ex = this.getException(exception);
		return Response.status(422).entity(new ErrorMessage(ex.getClass().getSimpleName(), ex.getMessage(), this.getMessage(exception))).build();
	}
	
	private Throwable getException(SQLException exception) {
		if(exception.getCause() != null) {
			return exception.getCause();
		}
		
		return exception;
	}
	
	private String getMessage(SQLException exception) {
		if(exception.getCause() != null) {
			return null;
		}
		
		return exception.getSQLState();
	}
}
