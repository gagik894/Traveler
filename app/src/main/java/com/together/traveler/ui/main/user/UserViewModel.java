package com.together.traveler.ui.main.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.model.User;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private final String TAG = "UserViewModel";

    private final MutableLiveData<User> user;
    private final MutableLiveData<List<Event>> events;
    private final MutableLiveData<Integer> state;
    private final MutableLiveData<Boolean> upcomingState;
    private final MutableLiveData<Boolean> selfPage;
    private boolean firstFetch;
    private String userId;
    private final ApiService apiService;

    public UserViewModel() {
        user = new MutableLiveData<>();
        state = new MutableLiveData<>();
        events = new MutableLiveData<>();
        upcomingState = new MutableLiveData<>(true);
        selfPage = new MutableLiveData<>(true);
        firstFetch = true;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<Integer> getState() {
        return state;
    }

    public LiveData<Boolean> getUpcomingState() {
        return upcomingState;
    }

    public MutableLiveData<Boolean> isSelfPage() {
        return selfPage;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public void setState(int state) {
        User user  = getUser().getValue();
        this.state.postValue(state);
        Log.i(TAG, "setState: " + upcomingState.getValue());
        if (user != null) {
            switch (state){
                case 0:
                    events.postValue(user.getEnrolledEvents().get(Boolean.TRUE.equals(upcomingState.getValue()) ?"upcoming":"past"));
                    break;
                case 1:
                    events.postValue(user.getFavoriteEvents().get(Boolean.TRUE.equals(upcomingState.getValue()) ?"upcoming":"past"));
                    break;
                case 2:
                    events.postValue(user.getUserEvents().get(Boolean.TRUE.equals(upcomingState.getValue()) ?"upcoming":"past"));
                    break;
            }
        }
    }

    public void setUpcomingState(boolean upcomingState) {
        this.upcomingState.setValue(upcomingState);
        setState(state.getValue());
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setUserId(String userId) {
        boolean fetchNeeded = !Objects.equals(userId, "self") || this.userId == null;
        if (!Objects.equals(userId, "")) {
            this.userId = userId;
            if (!Objects.equals(userId, "self")) {
                selfPage.setValue(false);
            }
            Log.d(TAG, "setUserId: " + userId);
        }
        if (fetchNeeded)
            fetchUser();
    }

    public void setAvatar(Bitmap bitmap){
        fetchChangeAvatar(bitmap);
    }


    private void fetchChangeAvatar(Bitmap bitmap){
        File file = saveBitmapToFile(bitmap);
        User current = user.getValue();
        if (!file.exists() || current==null) {
            // Handle error, file does not exist
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        apiService.changeAvatar(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    current.setAvatar(response.body());
                    user.setValue(current);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    public void fetchUser() {
        Log.i(TAG, "fetchUser: " + userId);
        apiService.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    User fetchedUser = response.body();
                    setUser(fetchedUser);
//                    user.postValue(fetchedUser);

                    if (Boolean.FALSE.equals(selfPage.getValue())) {
                        setState(2);
                        return;
                    }
                    if (state.getValue() != null)
                        setState(state.getValue());
                    state.postValue(state.getValue());
                    userId = fetchedUser != null ? fetchedUser.get_id() : null;
                    if (firstFetch) {
                        setState(0);
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

    private File saveBitmapToFile(Bitmap bitmap) {
        Context context = AppContext.getContext();
        File file = new File(context.getCacheDir(), "image.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}