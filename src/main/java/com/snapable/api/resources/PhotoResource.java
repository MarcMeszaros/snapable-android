package com.snapable.api.resources;

import com.snapable.api.SnapImage;
import com.snapable.api.models.Pager;
import com.snapable.api.models.Photo;
import retrofit.http.*;
import retrofit.mime.TypedString;

public interface PhotoResource {

	public static final String RESOURCE_NAME = "photo";

	@GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos();

    @GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
    	@Query("event") long eventId
    );

    @GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
        @Query("event") long eventId,
        @Query("streamable") boolean streamable
    );

    @GET("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: application/json")
    Photo getPhoto(
    	@Path("id") long id
    );

    @GET("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: image/jpeg")
    SnapImage getPhotoBinary(
    	@Path("id") long id,
    	@Query("size") String size
    );

    @POST("/"+RESOURCE_NAME+"/{id}/")
    @Headers({
        "Accept: application/json",
        "X-HTTP-Method-Override: PATCH" // retrofit doesn't support "PATCH" with response body
    })
    Photo patchPhoto(
        @Path("id") int id,
        @Body Photo data
    );

    @Multipart
    @POST("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Photo postPhoto(
        @Part("image") SnapImage photo,
        @Part("event") TypedString event,
        @Part("caption") TypedString caption
    );

    @Multipart
    @POST("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Photo postPhoto(
        @Part("image") SnapImage photo,
        @Part("event") TypedString event,
        @Part("guest") TypedString guest,
        @Part("caption") TypedString caption
    );

}