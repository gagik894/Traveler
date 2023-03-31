package com.together.traveler.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.together.traveler.data.LoginRepository;
import com.together.traveler.data.Result;
import com.together.traveler.data.model.LoggedInUser;
import com.together.traveler.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result<LoggedInUser> result = loginRepository.login(username, password);

                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
        thread.start();

    }

    public void signup(String username, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result<LoggedInUser> result = loginRepository.signup(username, password);

                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
        thread.start();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}