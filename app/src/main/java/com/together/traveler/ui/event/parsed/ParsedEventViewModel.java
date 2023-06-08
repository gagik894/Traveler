package com.together.traveler.ui.event.parsed;

import com.together.traveler.model.ParsedEvent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ParsedEventViewModel extends ViewModel {
    private final  MutableLiveData<ParsedEvent> data;

    public ParsedEventViewModel() {
        data = new MutableLiveData<>();
    }

    public void setData(ParsedEvent data) {
        this.data.setValue(data);

    }

    public LiveData<ParsedEvent> getData() {
        return data;
    }
}
