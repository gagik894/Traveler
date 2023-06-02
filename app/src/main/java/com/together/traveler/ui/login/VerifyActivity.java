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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.databinding.ActivitySignupVerifyBinding;
import com.together.traveler.model.User;
import com.together.traveler.ui.main.MainActivity;

public class VerifyActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;
    private ActivitySignupVerifyBinding binding;
    private CardView BottomView;
    private RelativeLayout BottomRelativeLayout;


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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        binding = ActivitySignupVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(RegisterViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("extras");
        String username = bundle.getString("username");
        String email = bundle.getString("email");
        String password = bundle.getString("password");
        String FCMToken = bundle.getString("FCMToken");
        String secCode = bundle.getString("secCode");
        registerViewModel.setUser(new User(username, email, password, FCMToken));
        registerViewModel.setSecCode(secCode);


        final EditText secCodeEditText = binding.verifyEtSecCode;
        final Button signupButton = binding.verifyBtnSignup;
        final ProgressBar loadingProgressBar = binding.signupPbLoading;
        final TextView toLoginButton = binding.verifyTvLogin;
        BottomView = binding.verifyViewBottom;
        BottomRelativeLayout = binding.verifyRlBottom;

        registerViewModel.getVerifyFormState().observe(this, verifyFormState -> {
            if (verifyFormState == null) {
                return;
            }
            signupButton.setEnabled(verifyFormState.isDataValid());
            if (verifyFormState.getSecCodeError() != null) {
                secCodeEditText.setError(getString(verifyFormState.getSecCodeError()));
            }
        });

        registerViewModel.getSignupResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            signupButton.setEnabled(true);
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
                registerViewModel.verifyDataChanged(secCodeEditText.getText().toString());
            }
        };

        secCodeEditText.addTextChangedListener(afterTextChangedListener);
        secCodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.signup();
            }
            return false;
        });

        signupButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);
            registerViewModel.signup();
        });
        toLoginButton.setOnClickListener(v->{
            Intent switchActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(switchActivityIntent);
        });


        secCodeEditText.setOnFocusChangeListener((view, b) -> changeView(b));
    }

    private void changeView(boolean b) {
        assert BottomView != null;
        assert BottomRelativeLayout != null;

        VerifyActivity.this.runOnUiThread(() -> {
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
        try {
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            startActivity(switchActivityIntent);
        } catch (Exception e) {
            Log.e("TAG", "updateUiWithUser: ", e);
        }
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
