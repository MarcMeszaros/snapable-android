package com.snapable.api.resources;

import java.io.InputStream;
import java.util.LinkedHashMap;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;

@EndPoint(SnapApi.api_host)
@Path("/private_v1/"+PhotoResource.RESOURCE_NAME+"/")
public interface PhotoResource {
	
	public static final String RESOURCE_NAME = "photo";
	
    @GET
    @Path("/{id}/")
    @HeaderParam(value="Accept", defaultValue="image/jpeg")
    InputStream getPhotoBinary(
    	@PathParam("id") long id
    );
    
    @POST
    @Path("/")
    LinkedHashMap<String, Object> uploadPhoto(
    	@MultiPartParam(value="image", contentType="image/jpeg", fileName="image.jpg") InputStream photo,
    	@MultiPartParam(value="event") String event,
    	@MultiPartParam(value="guest") String guest,
    	@MultiPartParam(value="type", defaultValue="/private_v1/type/6/") String type,
    	@MultiPartParam(value="caption", defaultValue="") String caption
    );
}