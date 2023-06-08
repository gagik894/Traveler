package com.together.traveler.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.ActivityChangePasswordBinding;
import com.together.traveler.model.User;
import com.together.traveler.ui.main.MainActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private ChangePasswordViewModel mViewModel;
    private ActivityChangePasswordBinding binding;
    private CardView BottomView;
    private ConstraintLayout BottomRelativeLayout;

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

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("extras");

        String email = bundle.getString("email");
        mViewModel.setEmail(email);

        final EditText passwordEditText = binding.changeEtPass;
        final EditText repeatPasswordEditText = binding.changeEtRepeatPass;
        final Button loginBtn = binding.changeBtnLogin;
        final ProgressBar loadingProgressBar = binding.signupPbLoading;
        BottomView = binding.verifyViewBottom;
        BottomRelativeLayout = binding.verifyRlBottom;


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
                mViewModel.passwordChanged(passwordEditText.getText(), repeatPasswordEditText.getText());
            }
        };


        repeatPasswordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);


        loginBtn.setOnClickListener(v -> {
            loginBtn.setEnabled(false);
            loadingProgressBar.setVisibility(View.VISIBLE);
            mViewModel.submitChange(passwordEditText.getText().toString());
        });

        passwordEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        repeatPasswordEditText.setOnFocusChangeListener((view, b) -> changeView(b));

        mViewModel.getPasswordsValid().observe(this, isValid->passwordEditText.setError(isValid?  null: getString(R.string.invalid_password)));
        mViewModel.getPasswordsSame().observe(this, same->{
            if (same){
                loginBtn.setEnabled(true);
            }else{
                loginBtn.setEnabled(false);
                repeatPasswordEditText.setError(getString(R.string.not_same_password));
            }
        });
        mViewModel.getLoginSuccess().observe(this, isSuccess->{
            if (isSuccess){
                updateUiWithUser();
            }
        });
    }

    private void changeView(boolean b) {
        assert BottomView != null;
        assert BottomRelativeLayout != null;

        ChangePasswordActivity.this.runOnUiThread(() -> {
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

    private void updateUiWithUser() {
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
