package ca.hashbrown.snapable.api.resources;

import com.snapable.api.SnapImage;
import com.snapable.api.private_v1.objects.Pager;

import ca.hashbrown.snapable.api.models.Event;

import retrofit.http.*;

public interface EventResource {

	public static final String RESOURCE_NAME = "event";

    @GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents();

    @GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents(
    	@Query("lat") float lat,
    	@Query("lng") float lng
    );

    @GET("/"+RESOURCE_NAME+"/search/")
    @Headers("Accept: application/json")
    Pager<Event> getEvents(
    	@Query("q") String query
    );

    @GET("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: application/json")
    Event getEvent(
    	@Path("id") long id
    );

    @GET("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: image/jpeg")
    SnapImage getEventPhotoBinary(
    	@Path("id") long id,
    	@Query("size") String size
    );
}