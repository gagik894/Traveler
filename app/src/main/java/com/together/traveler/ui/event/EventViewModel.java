package com.together.traveler.ui.event;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Card;

public class EventViewModel extends ViewModel {

    private final MutableLiveData<Card> data;

    public EventViewModel() {
        data = new MutableLiveData<>();
    }

    public void setData(Card data) {
        this.data.setValue(data);
    }

    public LiveData<Card> getData() {
        return data;
    }
}
