package com.snapable.api.resources;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;
import com.snapable.api.model.*;

@EndPoint(SnapApi.api_host)
@Path("/private_v1/"+EventResource.RESOURCE_NAME+"/")
@Consumes("application/json")
public interface EventResource {
	
	public static final String RESOURCE_NAME = "event";

    @GET
    @Path("/")
    Pager<Event[]> getEvents();
    
    @GET
    @Path("/{id}/")
    Event getEvent(
    	@PathParam("id") long id
    );
    
    @GET
    @Path("/{id}/")
    @Consumes("image/jpeg")
    android.graphics.Bitmap getEventPhotoBinary(
    	@PathParam("id") long id,
    	@QueryParam("size") String size
    );
}