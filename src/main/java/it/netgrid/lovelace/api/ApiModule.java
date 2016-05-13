package it.netgrid.lovelace.api;

import java.util.Locale;

import javax.xml.bind.JAXBException;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.cronutils.validator.CronValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.api.json.JSONUnmarshaller;

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

	@Provides
	@Singleton
	public CronValidator getCronValidator(CronDefinition definition) {
		return new CronValidator(definition);
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
	
//	@Provides
//	@Singleton
//	public JSONJAXBContext getJSONJAXBContext() throws JAXBException {
//		return new JSONJAXBContext();
//	}
	
	@Provides
	@Singleton
	public JSONMarshaller getJSONMarshaller(JSONJAXBContext context) throws JAXBException {
		return context.createJSONMarshaller();
	}
	
	@Provides
	@Singleton
	public JSONUnmarshaller getJSONUnmarshaller(JSONJAXBContext context) throws JAXBException {
		return context.createJSONUnmarshaller();
	}
}
