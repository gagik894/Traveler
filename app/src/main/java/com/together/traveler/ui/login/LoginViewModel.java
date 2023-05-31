package com.together.traveler.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.together.traveler.data.LoginRepository;
import com.together.traveler.data.Result;
import com.together.traveler.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
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
                String data = ((Result.Error) result).getError();
                loginResult.postValue(new LoginResult(data));
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
                String data = ((Result.Error) result).getError();
                loginResult.postValue(new LoginResult(data));
            }
        });
        thread.start();
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null,R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_password, null));
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

    public boolean isLoggedIn() {
        return loginRepository.isLoggedIn();
    }
}