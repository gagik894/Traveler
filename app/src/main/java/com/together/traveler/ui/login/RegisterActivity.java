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
import android.view.ViewTreeObserver;
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

import com.together.traveler.databinding.ActivitySignupBinding;
import com.together.traveler.model.User;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivitySignupBinding binding;
    private CardView BottomView;
    private RelativeLayout BottomRelativeLayout;
    private final String Tag = "RegisterActivity";
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        final View activityRootView = binding.getRoot();
        setContentView(activityRootView);

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

        registerViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(RegisterViewModel.class);

        emailEditText = binding.signupEtEmail;
        passwordEditText = binding.signupEtPassword;
        final EditText usernameEditText = binding.signupEtUsername;
        final EditText rPasswordEditText = binding.signupEtRPassword;
        final Button nextButton = binding.signupBtnSignup;
        final ProgressBar loadingProgressBar = binding.signupPbLoading;
        final TextView toLoginButton = binding.signupTvLogin;
        BottomView = binding.signupViewBottom;
        BottomRelativeLayout = binding.signupRlBottom;

        registerViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            nextButton.setEnabled(loginFormState.isDataValid());
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

        registerViewModel.getSecCode().observe(this, secCode -> {
            if (secCode == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            nextButton.setEnabled(true);
            updateUiWithUser(secCode, usernameEditText.getText().toString(), emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
            setResult(Activity.RESULT_OK);

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
                registerViewModel.signupDataChanged(usernameEditText.getText().toString(), emailEditText.getText().toString(),
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
            if (actionId == EditorInfo.IME_ACTION_DONE &&
                    registerViewModel.getLoginFormState().getValue() != null &&
                    registerViewModel.getLoginFormState().getValue().isDataValid()) {
                registerViewModel.fetchVerificationCode(emailEditText.getText().toString());
            }
            return false;
        });

        nextButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            nextButton.setEnabled(false);
            registerViewModel.fetchVerificationCode(emailEditText.getText().toString());
        });

        usernameEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        emailEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        passwordEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        rPasswordEditText.setOnFocusChangeListener((view, b) -> changeView(b));
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

    private void updateUiWithUser(String secCode, String username, String email, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("secCode", secCode);
        bundle.putString("username", username);
        bundle.putString("email", email);
        bundle.putString("password", password);

        Intent switchActivityIntent = new Intent(this, VerifyActivity.class);
        switchActivityIntent.putExtra("extras", bundle);

        startActivity(switchActivityIntent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


    private class CheckTextWatcher implements TextWatcher {
        private final String parameter;
        private final Timer timer;
        private final long DELAY = 700;
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
                    registerViewModel.fetchCheck(s, parameter);
                }
            };

            timer.schedule(timerTask, DELAY);
        }
    }

}