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
import it.netgrid.lovelace.model.StepStatus;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.Configuration;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.quartz.SchedulerUtils;

@Path("/tasks")
public class TaskStatusResource {

	public static final String INVALID_TASK = "task/id";
	
	private final CrudService<TaskStatus, Long> taskStatusService;
	private final CrudService<RunStatus, Long> runStatusService;
	private final CrudService<SchedulerStatus, Long> schedulerStatusService;
	private final SchedulerUtils schedulerUtils;
	private final Configuration config;
	
	@Inject
	public TaskStatusResource(
			Configuration config,
			CrudService<TaskStatus, Long> taskStatusService,
			SchedulerUtils schedulerUtils,
			CrudService<SchedulerStatus, Long> schedulerStatusService,
			CrudService<RunStatus, Long> runStatusService) {
		this.config = config;
		this.taskStatusService = taskStatusService;
		this.schedulerUtils = schedulerUtils;
		this.runStatusService = runStatusService;
		this.schedulerStatusService = schedulerStatusService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<TaskStatus> getTasks() throws IllegalArgumentException, SQLException {
		SchedulerStatus system = this.schedulerStatusService.read(this.config.getSchedulerId());
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
	@Path("{task}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus getTask(@PathParam(value="task") Long taskId) throws IllegalArgumentException, SQLException {
		TaskStatus retval = this.taskStatusService.read(taskId);
		return retval;
	}

	@PUT
	@Path("{task}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus updateTask(@PathParam(value="task") Long taskId, TaskStatus task) throws IllegalArgumentException, SQLException {
		task.setId(taskId);
		this.taskStatusService.update(task);
		TaskStatus retval = this.taskStatusService.read(task.getId());
		return retval;
	}
	
	@DELETE
	@Path("{task}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus deleteTask(@PathParam(value="task") Long taskId) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(taskId);
		this.taskStatusService.delete(task);
		return task;
	}

	@POST
	@Path("{task}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus manualStart(@PathParam(value="task") Long taskId) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(taskId);
		this.schedulerUtils.runNow(task);
		task = this.taskStatusService.read(taskId);
		return task;
	}
	
	@DELETE
	@Path("{task}/run")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public TaskStatus manualStop(@PathParam(value="task") Long taskId) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(taskId);
		this.schedulerUtils.stopNow(task);
		task = this.taskStatusService.read(taskId);
		return task;
	}

	@GET
	@Path("{task}/runs")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<RunStatus> getTaskRuns(@PathParam(value="task") Long taskId) throws IllegalArgumentException, SQLException {
		TaskStatus task = this.taskStatusService.read(taskId);
		List<RunStatus> retval = new ArrayList<RunStatus>();
		
		for(RunStatus run : task.getTaskRuns()) {
			retval.add(run);
		}
		
		return retval;
	}

	@GET
	@Path("{task}/runs/{run}/steps")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<StepStatus> getRunSteps(@PathParam(value="task") Long taskId, @PathParam(value="run") Long run) throws IllegalArgumentException, SQLException {
		List<StepStatus> retval = new ArrayList<StepStatus>();
		
		RunStatus runStatus = this.runStatusService.read(run);
		if(runStatus.getTaskStatus().getId() != taskId) {
			throw new IllegalArgumentException(INVALID_TASK);
		}
		
		for(StepStatus step : runStatus.getStepsStatus()) {
			retval.add(step);
		}
		
		return retval;
	}
}
