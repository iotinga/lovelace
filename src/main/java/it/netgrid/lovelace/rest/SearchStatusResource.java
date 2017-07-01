package it.netgrid.lovelace.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import it.netgrid.commons.data.BulkService;
import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.quartz.SchedulerUtils;

@Path("/search")
public class SearchStatusResource {
	
	private final CrudService<TaskStatus, Long> taskStatusService;
	private final BulkService<TaskStatus, Long> taskStatusBulk;
	private final SchedulerUtils schedulerUtils;
	
	@Inject
	public SearchStatusResource(
			BulkService<TaskStatus, Long> taskStatusBulk,
			SchedulerUtils schedulerUtils,
			CrudService<TaskStatus, Long> taskStatusService) {
		this.taskStatusBulk = taskStatusBulk;
		this.taskStatusService = taskStatusService;
		this.schedulerUtils = schedulerUtils;
	}

	@GET
	@Path("{query}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> query(@PathParam(value="query") String query) throws IllegalArgumentException, SQLException {
		return this.readTaskStatus(query);
	}

	@POST
	@Path("{query}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> manualStart(@PathParam(value="query") String query) throws IllegalArgumentException, SQLException {
		List<TaskStatus> tasks = this.readTaskStatus(query);
		List<TaskStatus> retval = new ArrayList<>();
		for(TaskStatus task : tasks) {
			this.schedulerUtils.runNow(task);
			retval.add(this.taskStatusService.read(task.getId()));
		}
		return retval;
	}
	
	@DELETE
	@Path("{query}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> manualStop(@PathParam(value="query") String query) throws IllegalArgumentException, SQLException {
		List<TaskStatus> tasks = this.readTaskStatus(query);
		List<TaskStatus> retval = new ArrayList<>();
		for(TaskStatus task : tasks) {
			this.schedulerUtils.stopNow(task);
			retval.add(this.taskStatusService.read(task.getId()));
		}
		return retval;
	}
	
	private List<TaskStatus> readTaskStatus(String name) throws IllegalArgumentException, SQLException {
		Map<String, Object> filter = new HashMap<>();
		filter.put(TaskStatus.NAME_FIELD_NAME, name);
		List<TaskStatus> result = this.taskStatusBulk.read(filter, 1L, null);
		if(result == null) {
			return new ArrayList<>();
		}
		return result;
	}

}
