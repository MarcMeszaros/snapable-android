package com.snapable.api.private_v1.resources;

import com.snapable.utils.SnapImage;
import com.snapable.api.private_v1.objects.*;

import retrofit.http.*;

public interface EventResource {

    @GET("/event/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents();

    @GET("/{uri}")
    @Headers("Accept: application/json")
    Pager<Event> getEventsNext(
            @Path(value="uri", encode=false) String uri
    );

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