package com.together.traveler.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.together.traveler.context.AppContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WebRequests {
    Context context = AppContext.getContext();
    private final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

    public void sendMultipartRequest(String url, String title, String description, String startDate,
                                     String startTime, String endDate, String endTime, Bitmap bitmap,
                                     String location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .build();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
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

                    String responseBody = "";
                    ResponseBody body = response.body();
                    if (body != null) {
                        responseBody = body.string();
                    }
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
                }
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
            }
        });

        // Use future.get() to wait for the task to complete and retrieve the result or exception
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
                return Objects.requireNonNull(response.body()).string();
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

    public String makeHttpPostRequest(String postUrl, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            try {
                Request request = new Request.Builder()
                        .url(postUrl)
                        .addHeader("auth-token", sharedPreferences.getString("auth_token", null))
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                return Objects.requireNonNull(response.body()).string();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("asd", "makeHttpPostRequest: ", e);
                return null;
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("asd", "makeHttpPostRequest: ", e);
        } finally {
            executor.shutdown();
        }
        return "";
    }


}
