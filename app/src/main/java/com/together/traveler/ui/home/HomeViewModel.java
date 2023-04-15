package com.together.traveler.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.requests.WebRequests;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> data;
    private final WebRequests webRequests;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        webRequests = new WebRequests();
        this.data.postValue(webRequests.makeHttpGetRequest("https://traveler-ynga.onrender.com/events"));
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }


    public LiveData<ArrayList<Event>> getData() {
        return data;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
