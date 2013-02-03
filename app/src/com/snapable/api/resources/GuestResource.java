package com.snapable.api.resources;

import org.codegist.crest.annotate.*;

import com.snapable.api.JsonEntityWriter;
import com.snapable.api.SnapApi;
import com.snapable.api.models.*;

@EndPoint(SnapApi.api_host)
@Path("/"+SnapApi.api_version+"/"+GuestResource.RESOURCE_NAME+"/")
@Consumes("application/json")
public interface GuestResource {
	
	public static final String RESOURCE_NAME = "guest";
	
	@GET
    @Path("/")
    Pager<Guest[]> getGuests();
    
    @GET
    @Path("/")
    Pager<Guest[]> getGuests(
    	@QueryParam("email") String email
    );
    
    @GET
    @Path("/{id}/")
    Guest getGuest(
    	@PathParam("id") long id
    );
    
    @PUT
    @Path("/{id}/")
    @EntityWriter(JsonEntityWriter.class)
    Guest putGuest(
    	@PathParam("id") long id,
    	@FormParam("name") String name
    );
    
    @POST
    @Path("/")
    @EntityWriter(JsonEntityWriter.class)
    Guest postGuest(
    	@FormParam("event") String event_uri,
    	@FormParam("type") String type_uri,
    	@FormParam("email") String email,
    	@FormParam("name") String name
    );

}