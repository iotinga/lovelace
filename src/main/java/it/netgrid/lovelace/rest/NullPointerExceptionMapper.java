package it.netgrid.lovelace.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import jakarta.inject.Singleton;

@Provider
@Singleton
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

	@Override
	public Response toResponse(NullPointerException exception) {
		return Response.status(Status.NO_CONTENT).build();
	}

}
