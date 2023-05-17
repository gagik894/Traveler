package com.together.traveler.data;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.model.LoginResponse;
import com.together.traveler.model.User;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;
import com.together.traveler.ui.main.home.HomeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final String TAG = "asd";
    private final ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

    public Result<String> signup(String username, String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        final CountDownLatch latch = new CountDownLatch(1);
        final Result[] result = new Result[1];
        apiService.auth("signupwithoutverification", requestBody).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    LoginResponse eventsResponse = response.body();
                    String auth_token = eventsResponse.getAuth_token();
                    result[0] = new Result.Success(auth_token);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String errorMessage = jsonObject.getString("error");
                        result[0] = new Result.Error(errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
                result[0] = new Result.Error(t.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }


//        String url = "https://traveler-ynga.onrender.com/auth/signupwithoutverification";
//
//        URL obj = null;
//        try {
//            obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            // Set request method to POST
//            con.setRequestMethod("POST");
//
//            // Set request headers
//            con.setRequestProperty("Content-Type", "application/json");
//
//            // Set request body
//            String requestBody = String.format("{\"username\": \"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
//            con.setDoOutput(true);
//            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
//            outputStream.writeBytes(requestBody);
//            outputStream.flush();
//            outputStream.close();
//
//            // Read response
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            JSONObject jsonObj = new JSONObject(response.toString());
//            Log.i(TAG, "fetch: " + jsonObj.getString("auth_token"));
//            String auth_token = jsonObj.getString("auth_token");
//            return  new Result.Success(auth_token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result.Error(new IOException("Error creating user"));
//        }
//    }


    public Result<String> login(String email, String password){

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        final CountDownLatch latch = new CountDownLatch(1);
        final Result[] result = new Result[1];
        apiService.auth("signin", requestBody).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    LoginResponse eventsResponse = response.body();
                    String auth_token = eventsResponse.getAuth_token();
                    result[0] = new Result.Success(auth_token);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String errorMessage = jsonObject.getString("error");
                        result[0] = new Result.Error(errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
                result[0] = new Result.Error(t.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];


//        String url = "https://traveler-ynga.onrender.com/auth/signin";
//
//        URL obj = null;
//        try {
//            obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            // Set request method to POST
//            con.setRequestMethod("POST");
//
//            // Set request headers
//            con.setRequestProperty("Content-Type", "application/json");
//
//            // Set request body
//            String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
//            con.setDoOutput(true);
//            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
//            outputStream.writeBytes(requestBody);
//            outputStream.flush();
//            outputStream.close();
//
//            // Read response
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            JSONObject jsonObj = new JSONObject(response.toString());
//            Log.i(TAG, "fetch: " + jsonObj.getString("auth_token"));
//            String auth_token = jsonObj.getString("auth_token");
//            return  new Result.Success(auth_token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result.Error(new IOException("Error creating user"));
//        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}