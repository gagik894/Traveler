package com.together.traveler.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.together.traveler.data.LoginRepository;
import com.together.traveler.data.Result;
import com.together.traveler.R;
import com.together.traveler.model.User;

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

    public void login(String email, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result<String> result = loginRepository.login(email, password);

                if (result instanceof Result.Success) {
                    String data = ((Result.Success<String>) result).getData();
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data)));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
        thread.start();
    }

    public void signup(String username, String email, String password) {
        // can be launched in a separate asynchronous job
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result<String> result = loginRepository.signup(username, email, password);

                if (result instanceof Result.Success) {
                    String data = ((Result.Success<String>) result).getData();
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data)));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
        thread.start();
    }

    public void loginDataChanged(String email, String password) {
        if (!isUserNameValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}