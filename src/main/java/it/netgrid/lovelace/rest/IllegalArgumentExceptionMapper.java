package it.netgrid.lovelace.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import jakarta.inject.Singleton;

import it.netgrid.lovelace.model.ErrorMessage;

@Provider
@Singleton
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
	
	@Override
	public Response toResponse(IllegalArgumentException exception) {
		return Response.status(422).entity(new ErrorMessage(exception.getClass().getSimpleName(), exception.getMessage(), null)).build();
	}
	
}
