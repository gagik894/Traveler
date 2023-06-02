package com.together.traveler.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.together.traveler.R;
import com.together.traveler.ui.main.MainActivity;
import com.together.traveler.context.AppContext;
import com.together.traveler.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private CardView bottomView;
    private RelativeLayout bottomRelativeLayout;
    private final String TAG = "asd";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        AppContext.init(this);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        final View activityRootView = binding.getRoot();
        setContentView(activityRootView);

        final TextInputEditText emailEditText = binding.loginEtEmail;
        final EditText passwordEditText = binding.loginEtPassword;
        final Button loginButton = binding.loginBtnlogin;
        final ProgressBar loadingProgressBar = binding.loginPbLoading;
        final TextView toSignupButton = binding.loginTvSignup;
        final ImageView topView = binding.loginIvLogo;
        bottomView = binding.loginViewBottom;
        bottomRelativeLayout = binding.loginRlBottom;
        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        final Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpened;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                boolean isOpen = keypadHeight > screenHeight * 0.15;
                if (isOpen != wasOpened) {
                    wasOpened = isOpen;
                    if (isOpen) {
                        // keyboard is opened
                    } else {
                        View v = getCurrentFocus();
                        if (v instanceof EditText) {
                            v.clearFocus();
                        }
                    }
                }
            }
        });

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        if(loginViewModel.isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            Log.i(TAG, "onCreate: change");
            finish();
        }

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
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
            loginButton.setEnabled(true);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                return;
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        toSignupButton.setOnClickListener(v->{
            Intent switchActivityIntent = new Intent(this, RegisterActivity.class);
            startActivity(switchActivityIntent);
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
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE&&
                    loginViewModel.getLoginFormState().getValue() != null &&
                    loginViewModel.getLoginFormState().getValue().isDataValid()) {
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            loginViewModel.login(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());

        });


        emailEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        passwordEditText.setOnFocusChangeListener((view, b) -> changeView(b));

        bottomView.startAnimation(slideUp);
        topView.startAnimation(slideDown);
    }


    private void changeView(boolean b) {
        assert bottomView != null;
        assert bottomRelativeLayout != null;

        LoginActivity.this.runOnUiThread(() -> {
            LinearLayout.LayoutParams param0 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1);

            if (b) {
                bottomView.setLayoutParams(param0);
                bottomRelativeLayout.setLayoutParams(param1);
            } else {
                bottomView.setLayoutParams(param1);
                bottomRelativeLayout.setLayoutParams(param0);
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        try {
            Log.d(TAG, "updateUiWithUser: ");
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            startActivity(switchActivityIntent);
        } catch (Exception e) {
            Log.e(TAG, "updateUiWithUser: ", e);
        }

    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}

