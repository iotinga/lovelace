package it.netgrid.lovelace.rest;

import java.sql.SQLException;
import java.util.ArrayList;
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
import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.RunStepStatus;
import it.netgrid.lovelace.model.SystemStatus;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.quartz.SchedulerUtils;

@Path("/tasks")
public class TaskStatusResource {

	public static final String INVALID_TASK = "task/id";
	
	private final CrudService<SystemStatus, Long> systemStatusService;
	private final CrudService<TaskStatus, Long> taskStatusService;
	private final CrudService<TaskRunStatus, Long> taskRunStatusService;
	private final SchedulerUtils schedulerUtils;
	private final Configuration config;
	
	@Inject
	public TaskStatusResource(
			Configuration config,
			CrudService<SystemStatus, Long> systemStatusService,
			CrudService<TaskStatus, Long> taskStatusService,
			SchedulerUtils schedulerUtils,
			CrudService<TaskRunStatus, Long> taskRunStatusService) {
		this.systemStatusService = systemStatusService;
		this.taskStatusService = taskStatusService;
		this.schedulerUtils = schedulerUtils;
		this.config = config;
		this.taskRunStatusService = taskRunStatusService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> getTasks() throws IllegalArgumentException, SQLException {
		SystemStatus system = this.systemStatusService.read(this.config.getSystemId());
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

	@GET
	@Path("{id}/runs")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskRunStatus> getTaskRuns(@PathParam(value="id") Long id) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(id);
		List<TaskRunStatus> retval = new ArrayList<TaskRunStatus>();
		
		for(TaskRunStatus run : task.getTaskRuns()) {
			retval.add(run);
		}
		
		return retval;
	}

	@GET
	@Path("{id}/runs/{run}/steps")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<RunStepStatus> getRunSteps(@PathParam(value="id") Long id, @PathParam(value="run") Long run) throws IllegalArgumentException, SQLException {
		List<RunStepStatus> retval = new ArrayList<RunStepStatus>();
		
		TaskRunStatus runStatus = this.taskRunStatusService.read(run);
		if(runStatus.getTask().getId() != id) {
			throw new IllegalArgumentException(INVALID_TASK);
		}
		
		for(RunStepStatus step : runStatus.getRunSteps()) {
			retval.add(step);
		}
		
		return retval;
	}
	
}
