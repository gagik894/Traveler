package com.together.traveler.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> data;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        createEventCardList(100);
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }

    private void createEventCardList(int n){
        this.data.setValue(Event.createCardList(n));
    }

    public LiveData<ArrayList<Event>> getData() {
        return data;
    }
}
