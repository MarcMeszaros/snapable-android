package com.snapable.api.resources;

import java.io.InputStream;
import java.util.LinkedHashMap;

import org.codegist.crest.annotate.*;

import com.snapable.api.SnapApi;
import com.snapable.api.model.*;

@EndPoint(SnapApi.api_host)
@Path("/private_v1/"+PhotoResource.RESOURCE_NAME+"/")
@Consumes("application/json")
public interface PhotoResource {
	
	public static final String RESOURCE_NAME = "photo";
	
    @GET
    @Path("/{id}/")
    @Consumes("image/jpeg")
    android.graphics.Bitmap getPhotoBinary(
    	@PathParam("id") long id
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