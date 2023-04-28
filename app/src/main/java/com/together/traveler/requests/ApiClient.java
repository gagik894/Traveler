package com.together.traveler.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.together.traveler.context.AppContext;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String TAG = "ApiClient";

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        Context context = AppContext.getContext();
        Log.i(TAG, "getRetrofitInstance: ");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);
        Log.i(TAG, "getRetrofitInstance: " + authToken);
        if (authToken != null) {
            // add the AuthenticationInterceptor to the OkHttpClient
            httpClient.addInterceptor(new AuthenticationInterceptor(authToken));
        }

        // add a logging interceptor to the OkHttpClient
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://traveler-ynga.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
}
