package com.together.traveler.ui.event;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventViewModel extends ViewModel {
    private final String TAG = "EventViewModel";

    private final MutableLiveData<Event> data;
    private String userId;
    private final ApiService apiService;


    public EventViewModel() {
        data = new MutableLiveData<>();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }
    public void setData(Event data) {
        this.data.setValue(data);
    }

    public void setUserId(String userId) {
        Log.d("asd", "setUserId: " + userId);
        this.userId = userId;
    }

    public void enroll() {
        Event current = data.getValue();
        if (current == null) {
            return;
        }
        String eventId = current.get_id();
        Call<Void> call = apiService.enroll(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    current.enroll();
                    data.setValue(current);
                } else {
                    Log.e(TAG, "enroll request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "enroll request failed with error: " + t.getMessage());
            }
        });
    }

    public void save() {
        Event current = data.getValue();
        if (current == null) {
            return;
        }
        String eventId = current.get_id();
        Call<Void> call = apiService.save(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    current.save();
                    data.setValue(current);
                } else {
                    Log.e(TAG, "save request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "save request failed with error: " + t.getMessage());
            }
        });
    }


    public LiveData<Event> getData() {
        return data;
    }

    public String getUserId() {
        return userId;
    }
}
