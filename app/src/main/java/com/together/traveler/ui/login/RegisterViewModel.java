package com.together.traveler.ui.login;

import android.text.Editable;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.R;
import com.together.traveler.data.LoginRepository;
import com.together.traveler.data.Result;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    private final String TAG = "RegisterViewModel";
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private final ApiService apiService;


    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(() -> {
            Result<String> result = loginRepository.login(email, password);

            if (result instanceof Result.Success) {
                String data = ((Result.Success<String>) result).getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data)));
            } else {
                loginResult.postValue(new LoginResult(R.string.login_failed));
            }
        });
        thread.start();
    }

    public void signup(String username, String email, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(() -> {
            Result<String> result = loginRepository.signup(username, email, password);

            if (result instanceof Result.Success) {
                String data = ((Result.Success<String>) result).getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data)));
            } else {
                loginResult.postValue(new LoginResult(R.string.login_failed));
            }
        });
        thread.start();
    }

    public void loginDataChanged(String username, String email, String password, String rPassword) {
        Log.i(TAG, "loginDataChanged: ");
        if (!isUsernameValid(username)){
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, null, null));
        }else if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null,null,  R.string.invalid_password, null));
        } else if (!password.equals(rPassword)) {
            loginFormState.setValue(new LoginFormState(null,null,  null, R.string.not_same_password));
            Log.i(TAG, "loginDataChanged: " + "no match");
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 0;
    }

    public boolean isLoggedIn() {
        return loginRepository.isLoggedIn();
    }

    public void check(Editable s, String parameter) {
        String finalParameter = parameter;

        if (Objects.equals(finalParameter, "email")){
            parameter = "Email";
            if(!isEmailValid(s.toString())) return;
        }else if(Objects.equals(finalParameter, "username")){
            parameter = "Username";
            if (!isUsernameValid(s.toString())) return;
        }

        apiService.checkRegister(parameter, s.toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    String checkResponse = response.body();

                } else {
                    if (Objects.equals(finalParameter, "username")){
                        loginFormState.setValue(new LoginFormState(
                                R.string.used_username, null, null,
                                null));
                    }else{
                        loginFormState.setValue(new LoginFormState(
                                null, R.string.used_email, null,
                            null));
                    }
                    Log.e(TAG, "fetch request failed with code: " + response.code() + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
