package com.together.traveler.ui.main.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final String TAG = "HomeViewModel";
    private final MutableLiveData<ArrayList<Event>> data;
    private String userId;
    private final ApiService apiService;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        getEvents();
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }


    public LiveData<ArrayList<Event>> getData() {
        return data;
    }


    public void getEvents() {
        Call<EventsResponse> call = apiService.getEvents();
        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call, @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    EventsResponse eventsResponse = response.body();
                    List<Event> events = eventsResponse != null ? eventsResponse.getData() : null;
                    data.postValue((ArrayList<Event>) events);
                    userId = eventsResponse != null ? eventsResponse.getUserId() : null;
                } else {
                    Log.e(TAG, "getEvents request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getEvents request failed with error: " + t.getMessage());
            }
        });
    }


    public String getUserId() {
        return userId;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
