package it.netgrid.lovelace.api;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.TaskRunStatus;
import it.netgrid.lovelace.model.SystemStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.model.RunStepStatus;

public class ApiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<CrudService<SystemStatus, Long>>() {}).to(SystemStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<TaskStatus, Long>>() {}).to(TaskStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<TaskRunStatus, Long>>() {}).to(TaskRunStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<RunStepStatus, Long>>() {}).to(RunStepStatusCrudService.class).in(Singleton.class);
	}

}
