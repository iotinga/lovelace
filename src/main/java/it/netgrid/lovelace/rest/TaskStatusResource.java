package it.netgrid.lovelace.rest;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.api.SystemStatusCrudService;
import it.netgrid.lovelace.model.SystemStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.quartz.SchedulerUtils;

@Path("/tasks")
public class TaskStatusResource {

	private final CrudService<SystemStatus, Long> systemStatusService;
	private final CrudService<TaskStatus, Long> taskStatusService;
	private final SchedulerUtils schedulerUtils;
	
	@Inject
	public TaskStatusResource(CrudService<SystemStatus, Long> systemStatusService,
			CrudService<TaskStatus, Long> taskStatusService,
			SchedulerUtils schedulerUtils) {
		this.systemStatusService = systemStatusService;
		this.taskStatusService = taskStatusService;
		this.schedulerUtils = schedulerUtils;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> getTasks() throws IllegalArgumentException, SQLException {
		SystemStatus system = this.systemStatusService.read(SystemStatusCrudService.DEFAULT_SYSTEM_ID);
		return system.getTasks();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus createTask(TaskStatus task) throws IllegalArgumentException, SQLException {
		this.taskStatusService.create(task);
		TaskStatus retval = this.taskStatusService.read(task.getId());
		return retval;
	}

	@GET
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus getTask(@PathParam(value="id") Long id) throws IllegalArgumentException, SQLException {
		TaskStatus retval = this.taskStatusService.read(id);
		return retval;
	}

	@PUT
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus updateTask(@PathParam(value="id") Long id, TaskStatus task) throws IllegalArgumentException, SQLException {
		task.setId(id);
		this.taskStatusService.update(task);
		TaskStatus retval = this.taskStatusService.read(task.getId());
		return retval;
	}
	
	@DELETE
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus deleteTask(@PathParam(value="id") Long id, TaskStatus task) throws IllegalArgumentException, SQLException {
		task.setId(id);
		this.taskStatusService.update(task);
		TaskStatus retval = this.taskStatusService.read(task.getId());
		return retval;
	}

	@POST
	@Path("{id}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus manualStart(@PathParam(value="id") Long id) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(id);
		this.schedulerUtils.runNow(task);
		task = this.taskStatusService.read(id);
		return task;
	}
	
	@DELETE
	@Path("{id}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus manualStop(@PathParam(value="id") Long id) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(id);
		this.schedulerUtils.stopNow(task);
		task = this.taskStatusService.read(id);
		return task;
	}
}
