package it.netgrid.lovelace.api;

import java.util.Locale;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import com.google.inject.TypeLiteral;

import it.netgrid.commons.data.BulkService;
import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.RunStatus;
import it.netgrid.lovelace.model.SchedulerStatus;
import it.netgrid.lovelace.model.TaskStatus;
import it.netgrid.lovelace.model.StepStatus;

public class ApiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<CrudService<SchedulerStatus, Long>>() {}).to(SchedulerStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<TaskStatus, Long>>() {}).to(TaskStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<RunStatus, Long>>() {}).to(RunStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<CrudService<StepStatus, Long>>() {}).to(StepStatusCrudService.class).in(Singleton.class);
		bind(new TypeLiteral<BulkService<TaskStatus, Long>>() {}).to(TaskStatusBulkService.class).in(Singleton.class);
		bind(StepService.class).to(StepServiceImpl.class).in(Singleton.class);
	}
	
	@Provides
	@Singleton
	public CronDefinition getCronDefinition() {
		return CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
	}
	
	@Provides
	@Singleton
	public CronDescriptor getCronDescriptor() {
		return CronDescriptor.instance(Locale.US);
	}
	
	@Provides
	@Singleton
	public CronParser getCronParser(CronDefinition definition) {
		return new CronParser(definition);
	}
}
