package com.together.traveler.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> data;

    public UserViewModel() {
        data = new MutableLiveData<>();
        data.setValue("Someone");
    }

    public void setData(String newData) {
        data.setValue(newData);
    }

    public LiveData<String> getData() {
        return data;
    }
}
