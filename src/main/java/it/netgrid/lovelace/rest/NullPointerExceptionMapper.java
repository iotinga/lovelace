package it.netgrid.lovelace.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;

@Provider
@Singleton
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

	@Override
	public Response toResponse(NullPointerException exception) {
		return Response.status(Status.NO_CONTENT).build();
	}

}
