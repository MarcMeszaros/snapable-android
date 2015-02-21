package com.snapable.api.private_v1.resources;

import com.snapable.utils.SnapImage;
import com.snapable.api.private_v1.objects.*;

import retrofit.http.*;
import retrofit.mime.TypedString;

public interface PhotoResource {

	@GET("/photo/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos();

    @GET("/{uri}")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
            @Path(value="uri", encode=false) String uri
    );

    @GET("/photo/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
    	@Query("event") long eventId
    );

    @GET("/photo/")
    @Headers("Accept: application/json")
    Pager<Photo> getPhotos(
        @Query("event") long eventId,
        @Query("streamable") boolean streamable
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

    @POST("/photo/{id}/")
    @Headers({
        "Accept: application/json",
        "X-HTTP-Method-Override: PATCH" // retrofit doesn't support "PATCH" with response body
    })
    Photo patchPhoto(
        @Path("id") int id,
        @Body Photo data
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