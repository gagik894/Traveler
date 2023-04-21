package com.together.traveler.ui.main.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.model.User;
import com.together.traveler.requests.WebRequests;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> data;
    private final MutableLiveData<Integer> state;
    private final WebRequests webRequests;
    private boolean firstFetch;

    public UserViewModel() {
        data = new MutableLiveData<>();
        state = new MutableLiveData<>();
        webRequests = new WebRequests();
        firstFetch = true;
        getUser();
    }

    public void getUser(){
        String result = webRequests.makeHttpGetRequest("https://traveler-ynga.onrender.com/events/user");
        if (result == null || result.equals(""))
            return;
        Gson gson = new Gson();
        User user = gson.fromJson(result, User.class);
        this.data.postValue(user);
        if (firstFetch){
            this.state.postValue(0);
            firstFetch = false;
        }

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

    public LiveData<User> getData() {
        return data;
    }
}
