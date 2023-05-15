package com.together.traveler.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.ActivitySignupBinding;
import com.together.traveler.ui.main.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivitySignupBinding binding;
    private CardView BottomView;
    private RelativeLayout BottomRelativeLayout;
    private final String Tag = "asd";

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText emailEditText = binding.signupEtEmail;
        final EditText passwordEditText = binding.signupEtPassword;
        final EditText usernameEditText = binding.signupEtUsername;
        final EditText rPasswordEditText = binding.signupEtRPassword;
        final Button loginButton = binding.signupBtnSignup;
        final ProgressBar loadingProgressBar = binding.signupPbLoading;
        final TextView toLoginButton = binding.signupTvLogin;
        BottomView = binding.signupViewBottom;
        BottomRelativeLayout = binding.signupRlBottom;

        registerViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
            if (loginFormState.getRepeatPasswordError() != null) {
                rPasswordEditText.setError(getString(loginFormState.getRepeatPasswordError()));
            }
        });

        registerViewModel.getLoginResult().observe(this, loginResult -> {
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

        toLoginButton.setOnClickListener(v->{
            Intent switchActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(switchActivityIntent);
        });

        InputFilter noSpaceAndSpecialCharFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuilder filteredBuilder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character) && !isSpecialCharacter(character)) {
                        filteredBuilder.append(character);
                    }
                }
                return filteredBuilder.toString();
            }

            private boolean isSpecialCharacter(char character) {
                String allowedSpecialCharacters = "_."; // Add other allowed special characters if needed
                return !Character.isLetterOrDigit(character) && allowedSpecialCharacters.indexOf(character) == -1;
            }
        };


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
                registerViewModel.loginDataChanged(usernameEditText.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString(), rPasswordEditText.getText().toString());
            }
        };

        CheckTextWatcher usernameCheck = new CheckTextWatcher("username");
        CheckTextWatcher emailCheck = new CheckTextWatcher("email");
        usernameEditText.addTextChangedListener(usernameCheck);
        emailEditText.addTextChangedListener(emailCheck);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        rPasswordEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.setFilters(new InputFilter[] { noSpaceAndSpecialCharFilter });
        rPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.signup(usernameEditText.getText().toString(),emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerViewModel.signup(usernameEditText.getText().toString(), emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        usernameEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        emailEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        passwordEditText.setOnFocusChangeListener((view, b) -> changeView(b));
    }

    private void changeView(boolean b) {
        assert BottomView != null;
        assert BottomRelativeLayout != null;

        RegisterActivity.this.runOnUiThread(() -> {
            LinearLayout.LayoutParams param0 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.5f);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1);

            if (b) {
                BottomView.setLayoutParams(param0);
            } else {
                BottomView.setLayoutParams(param1);
            }
        });
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getAuth_token();
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


    private class CheckTextWatcher implements TextWatcher {
        private final String parameter;
        private final Timer timer;
        private final long DELAY = 1000;
        private TimerTask timerTask;

        public CheckTextWatcher(String parameter) {
            this.parameter = parameter;
            this.timer = new Timer();
        }

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
            if (timerTask != null) {
                timerTask.cancel();
            }

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    registerViewModel.check(s, parameter);
                }
            };

            timer.schedule(timerTask, DELAY);
        }
    }

}