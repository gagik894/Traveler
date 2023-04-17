package com.together.traveler.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebRequests {
    Context context = AppContext.getContext();
    private final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void sendMultipartRequest(String url, String title, String description, String startDate,
                                     String startTime, String endDate, String endTime, Bitmap bitmap,
                                     String location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("description", description)
                    .addFormDataPart("startDate", startDate)
                    .addFormDataPart("startTime", startTime)
                    .addFormDataPart("endDate", endDate)
                    .addFormDataPart("endTime", endTime)
                    .addFormDataPart("location", location)
                    .addFormDataPart("image", "image.jpg",
                            RequestBody.create(MediaType.parse("application/octet-stream"), byteArrayOutputStream.toByteArray()))
                    .build();

            String authToken = sharedPreferences.getString("auth_token", null);
            Log.i("asd", "sendMultipartRequest: " + authToken);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("auth-token", authToken)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.i("asd", "sendMultipartRequest: " + response);

                String responseBody = response.body().string();
                Log.i("asd", "responseBody: " + responseBody);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    String name = responseHeaders.name(i);
                    String value = responseHeaders.value(i);
                    Log.i("asd", "responseHeader: " + name + " = " + value);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("asd", "sendMultipartRequest: ", e);
                // Handle failure
            } finally {
                executor.shutdown();
            }
        });
    }

    public String makeHttpGetRequest(String getUrl) {
        OkHttpClient client = new OkHttpClient();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            try {
                Request request = new Request.Builder()
                        .url(getUrl)
                        .addHeader("auth-token", sharedPreferences.getString("auth_token", null))
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("asd", "makeHttpGetRequest: ", e);
                return null;
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("asd", "makeHttpGetRequest: ", e);
        } finally {
            executor.shutdown();
        }
        return "";
    }

}
