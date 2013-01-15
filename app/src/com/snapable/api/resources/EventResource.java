package com.snapable.api.resources;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;
import com.snapable.api.models.*;

@EndPoint(SnapApi.api_host)
@Path("/"+SnapApi.api_version+"/"+EventResource.RESOURCE_NAME+"/")
@Consumes("application/json")
public interface EventResource {
	
	public static final String RESOURCE_NAME = "event";

    @GET
    @Path("/")
    Pager<Event[]> getEvents();
    
    @GET
    @Path("/")
    Pager<Event[]> getEvents(
    	@QueryParam("lat") float lat,
    	@QueryParam("lng") float lng
    );

    @GET
    @Path("/search/")
    Pager<Event[]> getEvents(
    	@QueryParam("q") String query
    );

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