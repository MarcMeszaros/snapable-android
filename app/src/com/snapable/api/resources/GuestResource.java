package com.snapable.api.resources;

import com.snapable.api.models.Guest;
import com.snapable.api.models.Pager;
import retrofit.http.*;

public interface GuestResource {

	public static final String RESOURCE_NAME = "guest";

	@GET("/guest/")
    @Headers("Accept: application/json")
    Pager<Guest> getGuests();

    @GET("/guest/")
    @Headers("Accept: application/json")
    Pager<Guest> getGuests(
    	@Query("email") String email,
    	@Query("event") long event_id
    );

    @GET("/guest/{id}/")
    @Headers("Accept: application/json")
    Guest getGuest(
    	@Path("id") long id
    );

    @PUT("/guest/{id}/")
    @Headers("Accept: application/json")
    Guest putGuest(
    	@Path("id") long id,
    	@Body Guest data
    );

    @POST("/guest/")
    @Headers("Accept: application/json")
    Guest postGuest(
    	@Body Guest data
    );

}