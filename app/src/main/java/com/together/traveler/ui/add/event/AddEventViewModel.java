package com.together.traveler.ui.add.event;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.requests.WebRequests;

import java.util.Objects;

public class AddEventViewModel extends ViewModel {
    private final MutableLiveData<Event> data;
    private final MutableLiveData<Boolean> isValid;
    private final WebRequests webRequests;
    public AddEventViewModel() {
        webRequests = new WebRequests();
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
        webRequests.sendMultipartRequest(url, event.getTitle(),event.getDescription(), event.getStartDate(),
                event.getStartTime(), event.getEndDate(), event.getEndTime(), event.getImageBitmap(), event.getLocation());
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
}