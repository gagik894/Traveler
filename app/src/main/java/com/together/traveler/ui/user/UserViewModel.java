package com.together.traveler.ui.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.R;
import com.together.traveler.model.Event;
import com.together.traveler.model.User;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> data;
    private final MutableLiveData<Integer> state;

    public UserViewModel() {
        data = new MutableLiveData<>();
        state = new MutableLiveData<>();
        state.setValue(0);
        createUser();
    }

    public void setState(int state){
        this.state.setValue(state);
    }

    public LiveData<Integer> getState(){
        return state;
    }

    public void setData(User data) {
        this.data.setValue(data);
    }

    private void createUser(){
        this.data.setValue(new User("username", "somewhere", 4.8f, R.drawable.default_user, Event.createCardList(100),Event.createCardList(5), Event.createCardList(1)));
    }

    public LiveData<User> getData() {
        return data;
    }
}
