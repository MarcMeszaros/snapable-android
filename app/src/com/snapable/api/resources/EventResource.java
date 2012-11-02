package com.snapable.api.resources;

import java.util.LinkedHashMap;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;

@EndPoint(SnapApi.api_host)
@Path("/private_v1/event/")
@Consumes("application/json")
public interface EventResource {

    @GET
    @Path("/")
    LinkedHashMap<String, Object> getEvents();
    
    @GET
    @Path("/{id}/")
    LinkedHashMap<String, Object> getEvent(
    	@PathParam("id") long id
    );
}