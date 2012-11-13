package com.snapable.api.resources;

import java.io.InputStream;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;
import com.snapable.api.models.*;

@EndPoint(SnapApi.api_host)
@Path("/"+SnapApi.api_version+"/"+PhotoResource.RESOURCE_NAME+"/")
@Consumes("application/json")
public interface PhotoResource {
	
	public static final String RESOURCE_NAME = "photo";
	
	@GET
    @Path("/")
    Pager<Photo[]> getPhotos();
    
    @GET
    @Path("/")
    Pager<Photo[]> getPhotos(
    	@QueryParam("event") long eventId
    );
    
    @GET
    @Path("/{id}/")
    Photo getPhoto(
    	@PathParam("id") long id
    );
	
    @GET
    @Path("/{id}/")
    @Consumes("image/jpeg")
    android.graphics.Bitmap getPhotoBinary(
    	@PathParam("id") long id,
    	@QueryParam("size") String size
    );
    
    @POST
    @Path("/")
    Photo uploadPhoto(
    	@MultiPartParam(value="image", contentType="image/jpeg", fileName="image.jpg") InputStream photo,
    	@MultiPartParam(value="event") String event,
    	@MultiPartParam(value="guest") String guest,
    	@MultiPartParam(value="type", defaultValue="/private_v1/type/6/") String type,
    	@MultiPartParam(value="caption", defaultValue="") String caption
    );
}