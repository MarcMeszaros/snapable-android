package ca.hashbrown.snapable.api.resources;

import com.snapable.api.private_v1.objects.Pager;

import ca.hashbrown.snapable.api.models.Guest;
import retrofit.http.*;

public interface GuestResource {

	public static final String RESOURCE_NAME = "guest";

	@GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Guest> getGuests();

    @GET("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Pager<Guest> getGuests(
    	@Query("email") String email,
    	@Query("event") long event_id
    );

    @GET("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: application/json")
    Guest getGuest(
    	@Path("id") long id
    );

    @PUT("/"+RESOURCE_NAME+"/{id}/")
    @Headers("Accept: application/json")
    Guest putGuest(
    	@Path("id") long id,
    	@Body Guest data
    );

    @POST("/"+RESOURCE_NAME+"/")
    @Headers("Accept: application/json")
    Guest postGuest(
    	@Body Guest data
    );

}