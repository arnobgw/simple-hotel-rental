package com.example.simplerentalapplication.rest;

import com.example.simplerentalapplication.model.ImageURL;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {
    @POST("/register")
    Call<User> registerUser(@Body User user);

    @POST("/login")
    Call<User> loginUser(@Body User user);

    @POST("/properties/{id}/assign/{date}")
    Call<Property> setBookingEndDate(@Path("id") int propertyId, @Path("date") long endDate, @Header("Session-ID") String sessionID);

    @POST("/properties")
    Call<Property> addProperty(@Header("Session-ID") String sessionID, @Body Property property);

    @PATCH("/properties/{id}")
    Call<Property> updateProperty(@Path("id") int propertyId, @Header("Session-ID") String sessionID, @Body Property property);

    @POST("/properties/url/{id}")
    Call<Property> addPropertyImageURL(@Path("id") int propertyId, @Header("Session-ID") String sessionID, @Body ImageURL url);

    @DELETE("/properties/{id}")
    Call<ResponseBody> removeProperty(@Path("id") int propertyId, @Header("Session-ID") String sessionID);

    @GET("/properties")
    Call<List<Property>> getAllProperties(@Header("Session-ID") String sessionID);

    @GET("/properties/available")
    Call<List<Property>> getAllAvailableProperties(@Header("Session-ID") String sessionID);

    @GET("/properties/rent")
    Call<List<Property>> getAllRentPropertiesType(@Header("Session-ID") String sessionID);

    @GET("/properties/purchase")
    Call<List<Property>> getAllPurchasePropertiesType(@Header("Session-ID") String sessionID);

    @GET("/properties/venue")
    Call<List<Property>> getAllVenuePropertiesType(@Header("Session-ID") String sessionID);

    @GET("/properties/rent/available")
    Call<List<Property>> getAllAvailableRentPropertiesType(@Header("Session-ID") String sessionID);

    @GET("/properties/purchase/available")
    Call<List<Property>> getAllAvailablePurchasePropertiesType(@Header("Session-ID") String sessionID);

    @GET("/properties/venue/available")
    Call<List<Property>> getAllAvailableVenuePropertiesType(@Header("Session-ID") String sessionID);
}

