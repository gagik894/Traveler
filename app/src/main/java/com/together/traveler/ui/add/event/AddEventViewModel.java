package com.together.traveler.ui.add.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddEventViewModel extends ViewModel {
    Context context = AppContext.getContext();
    private final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    private final MutableLiveData<Event> data;
    private final MutableLiveData<Boolean> isValid;

    public AddEventViewModel() {
        data = new MutableLiveData<>();
        isValid = new MutableLiveData<>(false);
        this.data.setValue(new Event());
    }

    public void dataChanged(String tittle, String location, String description, int count){
            Event current = data.getValue();
            Objects.requireNonNull(current).setTitle(tittle);
            Objects.requireNonNull(current).setLocation(location);
            Objects.requireNonNull(current).setDescription(description);
            Objects.requireNonNull(current).setTicketsCount(count);
            checkValid(current);
    }

    public void setStartDateAndTime(String date, String time) {
        Objects.requireNonNull(data.getValue()).setStartDate(date);
        Objects.requireNonNull(data.getValue()).setStartTime(time);
        this.data.setValue(data.getValue());
    }

    public void setEndDateAndTime(String date, String time) {
        Objects.requireNonNull(data.getValue()).setEndDate(date);
        Objects.requireNonNull(data.getValue()).setEndTime(time);
        this.data.setValue(data.getValue());
    }

    public void setEventImage(Bitmap image) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setImageBitmap(image);
        this.data.setValue(current);
        checkValid(current);
    }

    public void create(){
        Event event = data.getValue();
        String url = "https://traveler-ynga.onrender.com/events/add/event";
        sendMultipartRequest(url, event.getTitle(),event.getDescription(), event.getStartDate(),
                event.getStartTime(), event.getEndTime(), event.getImageBitmap());
        Log.i("asd", "create: " + Objects.requireNonNull(data.getValue()).getLocation() + data.getValue().getStartTime());
    }

    public LiveData<Event> getData() {
        return data;
    }

    public MutableLiveData<Boolean> isValid() {
        return isValid;
    }

    private void checkValid(Event current){
        isValid.setValue(!Objects.equals(current.getTitle(), "") && !Objects.equals(current.getLocation(), "")
                && !Objects.equals(current.getStartTime(), "") && !Objects.equals(current.getEndTime(), "")
                && !Objects.equals(current.getDescription(), "") && current.getTicketsCount()>0
                && current.getImageBitmap()!=null);
    }

    public void sendMultipartRequest(String url, String title, String description, String startDate,
                                     String startTime, String endTime, Bitmap bitmap) {
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
                    .addFormDataPart("endTime", endTime)
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



}