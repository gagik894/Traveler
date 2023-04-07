package com.together.traveler.ui.add.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;

import java.util.Objects;

public class AddEventViewModel extends ViewModel {
    private final MutableLiveData<Event> data;

    public AddEventViewModel() {
        data = new MutableLiveData<>();
        this.data.setValue(new Event());
    }

    public void dataChanged(String tittle, String location, String description, int count){
            Objects.requireNonNull(data.getValue()).setTitle(tittle);
            Objects.requireNonNull(data.getValue()).setLocation(location);
            Objects.requireNonNull(data.getValue()).setDescription(description);
            Objects.requireNonNull(data.getValue()).setTicketsCount(count);
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

    public void create(){
    }

    public LiveData<Event> getData() {
        return data;
    }
}