package com.together.traveler.web;

import com.together.traveler.model.CheckTicketResponse;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.model.LoginResponse;
import com.together.traveler.model.MapItem;
import com.together.traveler.model.Place;
import com.together.traveler.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("events")
    Call<EventsResponse> getEvents(@Query("fields") String fields, @Query("location") String location);

    @GET("{type}")
    Call<List<MapItem>> getMapItems(@Path("type") String type, @Query("fields") String fields);

    @GET("events/event/{id}")
    Call<Event> getEvent(@Path("id") String id, @Query("fields") String fields);

    @GET("places/place/{id}")
    Call<Place> getPlace(@Path("id") String id, @Query("fields") String fields);

    @GET("admin/places")
    Call<List<Place>> getAdminPlaces();

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

    @GET("{type}/categories")
    Call<List<String>> getCategories(
            @Path("type") String type
    );

    @PATCH("admin/places/verify/{id}")
    Call<String> verifyAdminPlace(
            @Path("id") String id
    );
    @DELETE("admin/places/delete/{id}")
    Call<String> deleteAdminPlace(
            @Path("id") String id
    );
    @POST("events/checkTicket")
    Call<CheckTicketResponse> checkTicket(@Body RequestBody requestBody);

    @POST("auth/{type}")
    Call<LoginResponse> auth(@Path("type") String type, @Body RequestBody requestBody);

    @POST("auth/check{param}/")
    Call<String> checkRegister(@Path("param") String param, @Body RequestBody requestBody);

    @POST("auth/sendVerificationCode")
    Call<ResponseBody> getVerificationCode(@Body RequestBody requestBody);

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
            @Part("category") RequestBody category,
            @Part List<MultipartBody.Part> tags
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
            @Part("category") RequestBody category,
            @Part("phone") RequestBody phone,
            @Part("url") RequestBody url,
            @Part List<MultipartBody.Part> openingTimes,
            @Part List<MultipartBody.Part> closingTimes
    );
}
