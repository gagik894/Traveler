package com.together.traveler.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.together.traveler.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final String TAG = "asd";


    public Result<String> signup(String username, String email, String password){
        String url = "https://traveler-ynga.onrender.com/auth/signupwithoutverification";

        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method to POST
            con.setRequestMethod("POST");

            // Set request headers
            con.setRequestProperty("Content-Type", "application/json");

            // Set request body
            String requestBody = String.format("{\"username\": \"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
            con.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObj = new JSONObject(response.toString());
            Log.i(TAG, "fetch: " + jsonObj.getString("auth_token"));
            String auth_token = jsonObj.getString("auth_token");
            return  new Result.Success(auth_token);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error creating user"));
        }
    }


    public Result<String> login(String email, String password){
        String url = "https://traveler-ynga.onrender.com/auth/signin";

        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method to POST
            con.setRequestMethod("POST");

            // Set request headers
            con.setRequestProperty("Content-Type", "application/json");

            // Set request body
            String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
            con.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObj = new JSONObject(response.toString());
            Log.i(TAG, "fetch: " + jsonObj.getString("auth_token"));
            String auth_token = jsonObj.getString("auth_token");
            return  new Result.Success(auth_token);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error creating user"));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}