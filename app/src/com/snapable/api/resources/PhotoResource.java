package com.snapable.api.resources;

import com.snapable.api.SnapImage;
import com.snapable.api.models.Pager;
import com.snapable.api.models.Photo;
import retrofit.http.*;
import retrofit.mime.TypedString;

public interface PhotoResource {

	public static final String RESOURCE_NAME = "photo";

	@GET("/photo/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos();

    @GET("/photo/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
    	@Query("event") long eventId
    );

    @GET("/photo/{id}/")
    @Headers("Accept: application/json")
    Photo getPhoto(
    	@Path("id") long id
    );

    @GET("/photo/{id}/")
    @Headers("Accept: image/jpeg")
    SnapImage getPhotoBinary(
    	@Path("id") long id,
    	@Query("size") String size
    );

    @Multipart
    @POST("/photo/")
    @Headers("Accept: application/json")
    Photo postPhoto(
        @Part("image") SnapImage photo,
        @Part("event") TypedString event,
        @Part("caption") TypedString caption
    );

    @Multipart
    @POST("/photo/")
    @Headers("Accept: application/json")
    Photo postPhoto(
        @Part("image") SnapImage photo,
        @Part("event") TypedString event,
        @Part("guest") TypedString guest,
        @Part("caption") TypedString caption
    );

}