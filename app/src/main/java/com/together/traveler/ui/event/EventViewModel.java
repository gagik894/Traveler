package com.together.traveler.ui.event;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.requests.WebRequests;


public class EventViewModel extends ViewModel {

    private final MutableLiveData<Event> data;
    private final WebRequests webRequests;


    public EventViewModel() {
        data = new MutableLiveData<>();
        webRequests = new WebRequests();
    }
    public void setData(Event data) {
        this.data.setValue(data);
    }

    public void enroll() {
        Event current = data.getValue();
        if (current == null)
            return;
        String Url = String.format("https://traveler-ynga.onrender.com/events/enroll/%s", current.get_id());
        webRequests.makeHttpGetRequest(Url);
        current.enroll();
        this.data.setValue(current);
    }

    public void save() {
        Event current = data.getValue();
        if (current == null)
            return;
        String Url = String.format("https://traveler-ynga.onrender.com/events/save/%s", current.get_id());
        webRequests.makeHttpGetRequest(Url);
        current.save();
        this.data.setValue(current);
    }

    public LiveData<Event> getData() {
        return data;
    }

}
