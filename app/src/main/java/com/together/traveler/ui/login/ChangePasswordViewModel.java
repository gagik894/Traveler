package com.together.traveler.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.LoginResponse;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordViewModel extends ViewModel {
    private final String TAG = "Change";
    private final ApiService apiService;
    private final MutableLiveData<String> secCode;
    private final MutableLiveData<Boolean> emailValid;
    private final MutableLiveData<Boolean> secCodeValid;
    private final MutableLiveData<Boolean> passwordsValid;
    private final MutableLiveData<Boolean> passwordsSame;
    private final MutableLiveData<Boolean> loginSuccess;
    private String email;

    public ChangePasswordViewModel() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        secCode = new MutableLiveData<>();
        secCodeValid = new MutableLiveData<>(false);
        emailValid = new MutableLiveData<>(false);
        passwordsValid = new MutableLiveData<>(false);
        passwordsSame = new MutableLiveData<>(false);
        loginSuccess = new MutableLiveData<>(false);
     }


    public MutableLiveData<Boolean> getEmailValid() {
        return emailValid;
    }

    public MutableLiveData<Boolean> getSecCodeValid() {
        return secCodeValid;
    }

    public MutableLiveData<Boolean> getPasswordsValid() {
        return passwordsValid;
    }

    public MutableLiveData<Boolean> getPasswordsSame() {
        return passwordsSame;
    }

    public MutableLiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
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

    private boolean isSecCodeValid(String secCode) {
        Log.i(TAG, "isSecCodeValid: " + secCode + " " + this.secCode.getValue());
        if (secCode == null || secCode.trim().length()!=6) {
            return false;
        }else {
            return BCrypt.checkpw(secCode, this.secCode.getValue());
        }
    }

    public void fetchCheck(Editable s) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        apiService.checkRegister("Email", requestBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    String checkResponse = response.body();
                    emailValid.setValue(false);
                } else {
                    emailValid.setValue(true);
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
                        secCode.setValue(response.body().string());
                        Log.i(TAG, "onResponse: " + secCode.getValue());
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

    public void emailChanged(Editable s) {
        if(isEmailValid(s.toString())){
            fetchCheck(s);
        }else{
            Log.i(TAG, "emailChanged: ");
            emailValid.setValue(false);
        }
    }

    public void secCodeChanged(Editable s) {
        secCodeValid.setValue(isSecCodeValid(s.toString()));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void passwordChanged(Editable pass, Editable rPass) {
        if (isPasswordValid(pass.toString()) && pass.toString().equals(rPass.toString())){
            passwordsValid.setValue(true);
            passwordsSame.setValue(true);
        }else{
            if (isPasswordValid(pass.toString())){
                passwordsValid.setValue(true);
                passwordsSame.setValue(false);
            }
        }
    }

    public void submitChange(String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email.trim().toLowerCase());
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        apiService.changePassword(requestBody).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    setLoggedInUser(response.body().getAuth_token(), response.body().getUserId());
                    loginSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {

            }
        });
    }
    private void setLoggedInUser(String auth_token, String userId) {
        Context context = AppContext.getContext();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", auth_token);
        editor.putString("userId", userId);
        editor.apply();

        Log.i(TAG, "setLoggedInUser: " + sharedPreferences.getString("auth_token", null));
    }
}
