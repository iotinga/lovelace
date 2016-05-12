package it.netgrid.lovelace.api;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import it.netgrid.commons.data.CrudService;
import it.netgrid.lovelace.model.SystemStatus;

public class ApiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<CrudService<SystemStatus, Long>>() {}).to(SystemStatusCrudService.class);
	}

}
