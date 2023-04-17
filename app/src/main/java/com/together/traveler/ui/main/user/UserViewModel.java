package com.together.traveler.ui.main.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.model.Event;
import com.together.traveler.model.User;
import com.together.traveler.requests.WebRequests;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> data;
    private final MutableLiveData<Integer> state;
    private final WebRequests webRequests;


    public UserViewModel() {
        data = new MutableLiveData<>();
        state = new MutableLiveData<>();
        webRequests = new WebRequests();
        state.setValue(0);
        getUser();
//        createUser();
    }

    private void getUser(){
        String result = webRequests.makeHttpGetRequest("https://traveler-ynga.onrender.com/events/user");
        if (result == null || result.equals(""))
            return;
        Gson gson = new Gson();
        User user = gson.fromJson(result, User.class);
        this.data.setValue(user);
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
        this.data.setValue(new User("1dMQgruv8yU_MVa3f4BX9idns4kZ8aAJQ", "username", "somewhere", 4.8f, Event.createCardList(100),Event.createCardList(5), Event.createCardList(1)));
    }

    public LiveData<User> getData() {
        return data;
    }
}
