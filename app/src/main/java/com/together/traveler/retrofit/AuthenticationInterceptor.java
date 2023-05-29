package com.together.traveler.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.together.traveler.context.AppContext;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    public AuthenticationInterceptor() {
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Context context = AppContext.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");
        Log.i("auth", "intercept: " + authToken);
        Request request = chain.request();
        Request.Builder builder = request.newBuilder()
                .header("auth-token", authToken);
        request = builder.build();
        return chain.proceed(request);
    }

}