package com.together.traveler.ui.main.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.model.User;
import com.together.traveler.requests.WebRequests;

import java.util.Objects;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> data;
    private final MutableLiveData<Integer> state;
    private final MutableLiveData<Boolean> selfPage;

    private final WebRequests webRequests;
    private boolean firstFetch;
    private String userId;

    public UserViewModel() {
        data = new MutableLiveData<>();
        state = new MutableLiveData<>();
        selfPage = new MutableLiveData<>(true);
        webRequests = new WebRequests();
        firstFetch = true;
    }

    public void getUser(){
        String url = String.format("https://traveler-ynga.onrender.com/events/profile/%s", userId);
        Log.d("asd", "getUser: " + url);
        String result = webRequests.makeHttpGetRequest(url);
        if (result == null || result.equals(""))
            return;
        Log.d("asd", "getUser: " + result);
        Gson gson = new Gson();
        User user = gson.fromJson(result, User.class);
        this.data.postValue(user);

        if (!Objects.equals(userId, "self")){
            this.state.postValue(2);
            return;
        }
        this.userId = user.get_id();
        if (firstFetch){
            this.state.postValue(0);
            firstFetch = false;
        }
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<Integer> getState(){
        return state;
    }

    public MutableLiveData<Boolean> isSelfPage() {
        return selfPage;
    }

    public LiveData<User> getData() {
        return data;
    }

    public void setState(int state){
        this.state.setValue(state);
    }

    public void setData(User data) {
        this.data.setValue(data);
    }

    public void setUserId(String userId){
        if (!Objects.equals(userId, "")) {
            this.userId = userId;
            if (!Objects.equals(userId, "self")){
                selfPage.setValue(false);
            }
            Log.d("asd", "setUserId: " + userId);
        }
        getUser();
    }

}
