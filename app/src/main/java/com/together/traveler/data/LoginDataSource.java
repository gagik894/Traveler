package com.together.traveler.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.together.traveler.model.User;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String TAG = "asd";

    public Result<User> signup(String username, String email, String password) {
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


    public Result<User> login(String email, String password) {
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