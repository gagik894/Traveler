package com.together.traveler.ui.event;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;

import java.util.Objects;

public class EventViewModel extends ViewModel {

    private final MutableLiveData<Event> data;

    public EventViewModel() {
        data = new MutableLiveData<>();
    }
    public void setData(Event data) {
        this.data.setValue(data);
    }

    public void enroll() {
        Objects.requireNonNull(data.getValue()).enroll();
        this.data.setValue(data.getValue());
    }

    public void save() {
        Objects.requireNonNull(data.getValue()).save();
        this.data.setValue(data.getValue());
    }

    public LiveData<Event> getData() {
        return data;
    }

}
