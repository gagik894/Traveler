package com.together.traveler.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.together.traveler.model.User;
import com.together.traveler.ui.login.AppContext;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;
    private final String TAG = "asd";
    Context context = AppContext.getContext();
    private final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    private final SharedPreferences.Editor editor = sharedPreferences.edit();
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private String auth_token = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;

    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        auth_token = sharedPreferences.getString("auth_token", null);
        return auth_token != null;
    }

    public void logout() {
        auth_token = null;
        dataSource.logout();

        editor.clear();
        editor.apply();
    }

    private void setLoggedInUser(String auth_token) {
        this.auth_token = auth_token;
        editor.putString("auth_token", auth_token);
        editor.apply();

        Log.i(TAG, "setLoggedInUser: " + sharedPreferences.getString("auth_token", null));
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<String> signup(String username, String email, String password) {
        // handle login
        Result<String> result = dataSource.signup(username, email, password);

        if (result instanceof Result.Success) {
            Log.i("asd", "login: " + result);
            setLoggedInUser(((Result.Success<String>) result).getData());
        }
        return result;
    }

    public Result<String> login(String username, String password) {
        // handle login
        Result<String> result = dataSource.login(username, password);

        if (result instanceof Result.Success) {
            Log.i("asd", "login: " + result);
            setLoggedInUser(((Result.Success<String>) result).getData());
        }
        return result;
    }
}