package com.together.traveler.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.together.traveler.context.AppContext;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String TAG = "ApiClient";

    private static Retrofit retrofit = null;
    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getRetrofitInstance() {
        Context context = AppContext.getContext();
        Log.i(TAG, "getRetrofitInstance: ");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);
        Log.i(TAG, "getRetrofitInstance: " + authToken);
        if (authToken != null) {
            httpClient.addInterceptor(new AuthenticationInterceptor());
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://traveler-ynga.onrender.com/")
//                    .baseUrl("http://localhost:3333/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();


        return retrofit;
    }
}
