package com.together.traveler.requests;

import android.content.Context;
import android.content.SharedPreferences;

import com.together.traveler.context.AppContext;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        Context context = AppContext.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (authToken != null) {
                httpClient.addInterceptor(new AuthenticationInterceptor(authToken));
            }
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://traveler-ynga.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}