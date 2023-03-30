package com.together.traveler.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.MainActivity;
import com.together.traveler.R;
import com.together.traveler.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    CardView BottomView;
    RelativeLayout BottomRelativeLayout;
    String Tag = "ASH5";
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        com.together.traveler.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        BottomView = binding.loginViewBottom;
        BottomRelativeLayout = binding.rlBottom;


        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });


        usernameEditText.setOnFocusChangeListener((view, b) -> changeView(b));

        passwordEditText.setOnFocusChangeListener((view, b) -> changeView(b));

    }




    void changeView(boolean b){
        assert BottomView != null;
        assert BottomRelativeLayout != null;
        for (float i = 1; i > 0; i-=0.01) {
            LinearLayout.LayoutParams param0 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    i);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1-i);

            if(b){
                BottomView.setLayoutParams(param0);
                BottomRelativeLayout.setLayoutParams(param1);
            }
            else{
                BottomView.setLayoutParams(param1);
                BottomRelativeLayout.setLayoutParams(param0);
            }
            Log.i(Tag, "changeView: "+ i);

        }

    }
    private void updateUiWithUser(LoggedInUserView model) {
        try {
            String welcome = getString(R.string.welcome) + model.getDisplayName();
            // TODO : initiate successful logged in experience
            Log.d(Tag, "updateUiWithUser: ");
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            startActivity(switchActivityIntent);
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.e(Tag, "updateUiWithUser: ", e);
        }

    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}