package com.snapable.api.resources;

import com.snapable.api.SnapImage;
import com.snapable.api.models.Event;
import com.snapable.api.models.Pager;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

public interface EventResource {

	public static final String RESOURCE_NAME = "event";

    @GET("/event/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents();

    @GET("/event/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents(
    	@Query("lat") float lat,
    	@Query("lng") float lng
    );

    @GET("/event/search/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents(
    	@Query("q") String query
    );

    @GET("/event/{id}/")
    @Headers("Accept: application/json")
    Event getEvent(
    	@Path("id") long id
    );

    @GET("/event/{id}/")
    @Headers("Accept: image/jpeg")
    SnapImage getEventPhotoBinary(
    	@Path("id") long id,
    	@Query("size") String size
    );
}