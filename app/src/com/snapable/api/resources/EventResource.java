package com.snapable.api.resources;

import java.io.InputStream;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;
import com.snapable.api.model.*;

@EndPoint(SnapApi.api_host)
@Path("/private_v1/"+EventResource.RESOURCE_NAME+"/")
public interface EventResource {
	
	public static final String RESOURCE_NAME = "event";

    @GET
    @Path("/")
    @Consumes("application/json")
    Pager<Event[]> getEvents();
    
    @GET
    @Path("/{id}/")
    @Consumes("application/json")
    Event getEvent(
    	@PathParam("id") long id
    );
    
    @GET
    @Path("/{id}/")
    @HeaderParam(value="Accept", defaultValue="image/jpeg")
    InputStream getEventPhotoBinary(
    	@PathParam("id") long id,
    	@QueryParam("size") String size
    );
}