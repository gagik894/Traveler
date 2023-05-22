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

    private final MutableLiveData<User> user;
    private final MutableLiveData<Integer> state;
    private final MutableLiveData<Boolean> selfPage;
    private boolean firstFetch;
    private String userId;
    private final ApiService apiService;

    public UserViewModel() {
        user = new MutableLiveData<>();
        state = new MutableLiveData<>();
        selfPage = new MutableLiveData<>(true);
        firstFetch = true;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public void fetchUser() {
        Log.i(TAG, "fetchUser: " + userId);
        apiService.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    User user = response.body();
                    UserViewModel.this.user.postValue(user);

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
                    Log.e(TAG, "fetchUser request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchUser request failed with error: " + t.getMessage());
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

    public LiveData<User> getUser() {
        return user;
    }

    public void setState(int state) {
        this.state.setValue(state);
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setUserId(String userId) {
        boolean fetch = !Objects.equals(userId, "self") || this.userId == null;
        if (!Objects.equals(userId, "")) {
            this.userId = userId;
            if (!Objects.equals(userId, "self")) {
                selfPage.setValue(false);
            }
            Log.d(TAG, "setUserId: " + userId);
        }
        if (fetch)
            fetchUser();
    }

}