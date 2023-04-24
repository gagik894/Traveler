package com.together.traveler.ui.main.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.User;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private final String TAG = "UserViewModel";

    private final MutableLiveData<User> data;
    private final MutableLiveData<Integer> state;
    private final MutableLiveData<Boolean> selfPage;
    private boolean firstFetch;
    private String userId;
    private final ApiService apiService;

    public UserViewModel() {
        data = new MutableLiveData<>();
        state = new MutableLiveData<>();
        selfPage = new MutableLiveData<>(true);
        firstFetch = true;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public void getUser() {
        Call<User> call = apiService.getUser(userId);
        Log.i(TAG, "getUser: " + userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    User user = response.body();
                    data.postValue(user);

                    if (Boolean.FALSE.equals(selfPage.getValue())) {
                        state.postValue(2);
                        return;
                    }

                    userId = user != null ? user.get_id() : null;
                    if (firstFetch) {
                        state.postValue(0);
                        firstFetch = false;
                    }
                } else {
                    Log.e(TAG, "getUser request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "getUser request failed with error: " + t.getMessage());
            }
        });
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<Integer> getState() {
        return state;
    }

    public MutableLiveData<Boolean> isSelfPage() {
        return selfPage;
    }

    public LiveData<User> getData() {
        return data;
    }

    public void setState(int state) {
        this.state.setValue(state);
    }

    public void setData(User data) {
        this.data.setValue(data);
    }

    public void setUserId(String userId) {
        if (!Objects.equals(userId, "")) {
            this.userId = userId;
            if (!Objects.equals(userId, "self")) {
                selfPage.setValue(false);
            }
            Log.d(TAG, "setUserId: " + userId);
        }
        getUser();
    }

}