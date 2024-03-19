package it.netgrid.lovelace.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

@Path("/info")
public class InfoResource {

    private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

    @GET
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Operation( description ="Informations about the services")
    public Response get(@Context HttpHeaders httpHeaders) {
        logger.info("Info requested");
        return Response.ok("CIAO").build();
    }

}
