package com.together.traveler.ui.main.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private final MutableLiveData<ArrayList<String>> categories;

    private final MutableLiveData<Event> mapSelectedEvent;
    private String userId;
    private final ApiService apiService;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        mapSelectedEvent = new MutableLiveData<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        fetchEvents();
        fetchCategories();
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }

    public void setMapSelectedEvent(int position){
        this.mapSelectedEvent.setValue(this.getData().getValue().get(position));
    }

    public LiveData<ArrayList<Event>> getData() {
        return data;
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public void fetchEvents() {
        apiService.getEvents().enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call, @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    EventsResponse eventsResponse = response.body();
                    List<Event> events = eventsResponse != null ? eventsResponse.getData() : null;
                    data.postValue((ArrayList<Event>) events);
                    userId = eventsResponse != null ? eventsResponse.getUserId() : null;
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() +response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }

    private void fetchCategories(){
        apiService.getEventCategories().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categoriesResponse = response.body();
                    Log.i(TAG, "onResponse: " + categoriesResponse);
                    categories.setValue((ArrayList<String>) categoriesResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle the error
            }
        });
    }

    public String getUserId() {
        return userId;
    }

    public MutableLiveData<Event> getMapSelectedEvent() {
        return mapSelectedEvent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
