package com.together.traveler.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.together.traveler.data.model.LoggedInUser;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String TAG = "asd";

    public Result<LoggedInUser> signup(String email, String password) {
        final Result[] result = new Result[1];
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(email, password);
            Thread thread = new Thread(() -> {
                try {
                    AuthResult authResult = Tasks.await(task);
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    Log.i(TAG, "run: " + Objects.requireNonNull(firebaseUser).getEmail());
                    LoggedInUser user = new LoggedInUser(
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
        }catch (Exception e){
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}