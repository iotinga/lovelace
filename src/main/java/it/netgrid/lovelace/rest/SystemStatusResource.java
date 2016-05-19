package it.netgrid.lovelace.rest;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.model.TaskStatus;

@Path("/")
public class SystemStatusResource {
	
	private final Configuration config;
	private final CrudService<SchedulerStatus, Long> schedulerStatusService;
	
	@Inject
	public SystemStatusResource(CrudService<SchedulerStatus, Long> schedulerStatusService, Configuration config) {
		this.schedulerStatusService = schedulerStatusService;
		this.config = config;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public SchedulerStatus get() throws IllegalArgumentException, SQLException {
		return this.schedulerStatusService.read(this.config.getSchedulerId());
	}


	@GET
	@Path("/tasks")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> getTasks() throws IllegalArgumentException, SQLException {
		SchedulerStatus system = this.schedulerStatusService.read(this.config.getSchedulerId());
		return system.getTasks();
	}
	
}
