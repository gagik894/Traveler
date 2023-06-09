package com.together.traveler.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.LoginResponse;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

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

    public Result<String> signup(String username, String email, String password, String FCMToken) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);
            json.put("FCMToken", FCMToken);
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
                    LoginResponse loginResponse = response.body();
                    String auth_token = loginResponse.getAuth_token();
                    result[0] = new Result.Success(auth_token);
                    saveUserId(loginResponse.getUserId());
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


    public Result<String> login(String email, String password, String FCMToken){
        Log.i(TAG, "login: " + FCMToken);
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
            json.put("FCMToken", FCMToken);
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
                    LoginResponse loginResponse = response.body();
                    String auth_token = loginResponse.getAuth_token();
                    result[0] = new Result.Success(auth_token);
                    saveUserId(loginResponse.getUserId());
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

    private void saveUserId(String userId) {
        Context context = AppContext.getContext();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    public void logout() {
        // TODO: revoke authentication
    }
}