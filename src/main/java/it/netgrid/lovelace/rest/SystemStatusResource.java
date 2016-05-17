package it.netgrid.lovelace.rest;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.api.SystemStatusCrudService;
import it.netgrid.lovelace.model.SystemStatus;

@Path("/")
public class SystemStatusResource {
	
	private final CrudService<SystemStatus, Long> systemStatusService;
	
	@Inject
	public SystemStatusResource(CrudService<SystemStatus, Long> systemStatusService) {
		this.systemStatusService = systemStatusService;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public SystemStatus get() throws IllegalArgumentException, SQLException {
		return this.systemStatusService.read(SystemStatusCrudService.DEFAULT_SYSTEM_ID);
	}
	
	
}
