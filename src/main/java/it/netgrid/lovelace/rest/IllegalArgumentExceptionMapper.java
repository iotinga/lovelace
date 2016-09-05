package it.netgrid.lovelace.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;

import it.netgrid.lovelace.model.ErrorMessage;

@Provider
@Singleton
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
	
	@Override
	public Response toResponse(IllegalArgumentException exception) {
		return Response.status(422).entity(new ErrorMessage(exception.getClass().getSimpleName(), exception.getMessage(), null)).build();
	}
	
}
