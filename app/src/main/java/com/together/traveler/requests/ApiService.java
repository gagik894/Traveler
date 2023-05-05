package com.together.traveler.requests;

import com.together.traveler.model.CheckTicketResponse;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.model.Place;
import com.together.traveler.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @GET("events")
    Call<EventsResponse> getEvents();

    @GET("places")
    Call<List<Place>> getPlaces();

    @GET("events/profile/{userId}")
    Call<User> getUser(
            @Path("userId") String userId
    );

    @GET("events/enroll/{eventId}")
    Call<Void> enroll(
            @Path("eventId") String eventId

    );

    @GET("events/save/{eventId}")
    Call<Void> save(
            @Path("eventId") String eventId
    );

    @GET("events/categories")
    Call<List<String>> getEventCategories();

    @GET("places/categories")
    Call<List<String>> getPlaceCategories();

    @POST("events/checkTicket")
    Call<CheckTicketResponse> checkTicket(@Body RequestBody requestBody);


    @Multipart
    @POST("events/add/event")
    Call<ResponseBody> uploadEventFile(
            @Part MultipartBody.Part filePart,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("startDate") RequestBody startDate,
            @Part("endDate") RequestBody endDate,
            @Part("location") RequestBody location,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("category") RequestBody requestBodyCategory
    );
    @Multipart
    @POST("places/add")
    Call<ResponseBody> uploadPlaceFile(
            @Part MultipartBody.Part filePart,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("category") RequestBody requestBodyCategory
    );
}
