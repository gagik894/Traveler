package com.together.traveler.ui.main.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.requests.WebRequests;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> data;
    private final WebRequests webRequests;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        webRequests = new WebRequests();
        getEvents();
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }


    public LiveData<ArrayList<Event>> getData() {
        return data;
    }

    private void getEvents(){
        String result = webRequests.makeHttpGetRequest("https://traveler-ynga.onrender.com/events");
        Log.i("asd", "getEvents: " + result);
        Gson gson = new Gson();
        if (result == null || result.equals(""))
            return;
        EventsResponse response = gson.fromJson(result, EventsResponse.class);
        List<Event> events = response.getData();
        this.data.postValue((ArrayList<Event>) events);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
