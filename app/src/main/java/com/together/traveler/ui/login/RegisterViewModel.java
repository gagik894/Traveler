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
import com.together.traveler.model.User;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    private final String TAG = "RegisterViewModel";
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginFormState> verifyFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> signUpResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private final ApiService apiService;
    private final User user;
    private MutableLiveData<String> secCode = new MutableLiveData<>();

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        user = new User();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginFormState> getVerifyFormState() {
        return verifyFormState;
    }

    LiveData<LoginResult> getSignupResult() {
        return signUpResult;
    }

    LiveData<String> getSecCode() {
        return secCode;
    }

    public User getUser() {
        return user;
    }

    public void setSecCode(String secCode) {
        this.secCode.setValue(secCode);
    }

    public void setUser(User user) {
        this.user.setPassword(user.getPassword());
        this.user.setUsername(user.getUsername());
        this.user.setEmail(user.getEmail());
    }

    public void signup() {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(() -> {
            Result<String> result = loginRepository.signup(user.getUsername(), user.getEmail(), user.getPassword());

            if (result instanceof Result.Success) {
                String data = ((Result.Success<String>) result).getData();
                signUpResult.postValue(new LoginResult(new LoggedInUserView(data)));
            } else {
                signUpResult.postValue(new LoginResult(R.string.login_failed));
            }
        });
        thread.start();
    }

    public void signupDataChanged(String username, String email, String password, String rPassword) {
        Log.i(TAG, "signupDataChanged: ");
        if (!isUsernameValid(username)){
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, null, null));
        }else if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null,null,  R.string.invalid_password, null));
        } else if (!password.equals(rPassword)) {
            loginFormState.setValue(new LoginFormState(null,null,  null, R.string.not_same_password));
            Log.i(TAG, "signupDataChanged: " + "no match");
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void verifyDataChanged(String secCode) {
        Log.i(TAG, "verifyDataChanged: ");
        if (!isSecCodeValid(secCode)){
            verifyFormState.setValue(new LoginFormState(R.string.invalid_sec_code));
        }else {
            verifyFormState.setValue(new LoginFormState(true));
        }
    }

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

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 0;
    }

    private boolean isSecCodeValid(String secCode) {
        Log.i(TAG, "isSecCodeValid: " + secCode + " " + this.secCode.getValue());
        if (secCode == null || secCode.trim().length()!=6) {
            return false;
        }else {
            return BCrypt.checkpw(secCode, this.secCode.getValue());
        }
    }

    public boolean isLoggedIn() {
        return loginRepository.isLoggedIn();
    }

    public void fetchCheck(Editable s, String parameter) {
        final String finalParameter = parameter;
        JSONObject json = new JSONObject();

        if (Objects.equals(finalParameter, "email")){
            parameter = "Email";
            if(!isEmailValid(s.toString())) return;
        }else if(Objects.equals(finalParameter, "username")){
            parameter = "Username";
            if (!isUsernameValid(s.toString())) return;
        }

        try {
            json.put(finalParameter, s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        apiService.checkRegister(parameter, requestBody).enqueue(new Callback<String>() {
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

    public void fetchVerificationCode(String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        apiService.getVerificationCode(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        RegisterViewModel.this.secCode.setValue(response.body().string());
                        Log.i(TAG, "onResponse: " + RegisterViewModel.this.secCode.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "fetch request failed with code: " + response.code() + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
