package com.together.traveler.requests;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private final String authToken;

    public AuthenticationInterceptor(String authToken) {
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder()
                .header("auth-token", authToken);
        request = builder.build();
        return chain.proceed(request);
    }
}