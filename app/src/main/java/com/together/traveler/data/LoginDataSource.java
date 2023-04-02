package com.together.traveler.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.together.traveler.model.User;

import org.json.JSONException;
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

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
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

    public Result<User> signupWithFirebase(String username, String email, String password){
        final CompletableFuture<Result<User>> future = new CompletableFuture<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(email, password);

        task.addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                // Set the additional fields in the Firebase user's profile
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(profileTask -> {
                            if (profileTask.isSuccessful()) {
                                User user = new User(
                                        firebaseUser.getUid(),
                                        firebaseUser.getEmail(),
                                        username);
                                future.complete(new Result.Success<>(user));
                            } else {
                                future.complete(new Result.Error(new IOException("Error updating user profile")));
                            }
                        });
            } else {
                future.complete(new Result.Error(new IOException("Error creating user")));
            }
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            return new Result.Error(new IOException("Error creating user", e));
        }
    }


    public Result<User> loginWithFirebase(String email, String password) {
        final Result[] result = new Result[1];
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
            Thread thread = new Thread(() -> {
                try {
                    AuthResult authResult = Tasks.await(task);
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    Log.i(TAG, "run: " + Objects.requireNonNull(firebaseUser).getEmail());
                    User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail());
                    result[0] = new Result.Success<>(user);
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                    result[0] = new Result.Error(new IOException("Error logging in", e));
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result[0];
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}