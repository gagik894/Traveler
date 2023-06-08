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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.databinding.ActivityCheckEmailBinding;

public class CheckEmailActivity extends AppCompatActivity {
    private ChangePasswordViewModel mViewModel;
    private ActivityCheckEmailBinding binding;
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

        binding = ActivityCheckEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);


        final EditText secCodeEditText = binding.checkEtSecCode;
        final EditText emailEditText = binding.checkEtEmail;
        final Button nextButton = binding.checkBtnNext;
        final Button sendCodeButton = binding.checkBtnSendCode;
        final TextView toLoginButton = binding.verifyTvLogin;
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
                mViewModel.emailChanged(s);
            }
        };
        TextWatcher secCodeTextWatcher = new TextWatcher() {
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
                mViewModel.secCodeChanged(s);
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        secCodeEditText.addTextChangedListener(secCodeTextWatcher);
        sendCodeButton.setOnClickListener(v->{
            mViewModel.fetchVerificationCode(emailEditText.getText().toString());
            sendCodeButton.setEnabled(false);
            emailEditText.setEnabled(false);
            secCodeEditText.setEnabled(true);
        });

        nextButton.setOnClickListener(v -> {
            nextButton.setEnabled(false);
            updateUiWithUser(emailEditText.getText().toString());
        });
        toLoginButton.setOnClickListener(v->{
            Intent switchActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(switchActivityIntent);
        });

        secCodeEditText.setOnFocusChangeListener((view, b) -> changeView(b));
        emailEditText.setOnFocusChangeListener((view, b) -> changeView(b));

        mViewModel.getEmailValid().observe(this, sendCodeButton::setEnabled);
        mViewModel.getSecCodeValid().observe(this, nextButton::setEnabled);
    }

    private void changeView(boolean b) {
        assert BottomView != null;
        assert BottomRelativeLayout != null;

        CheckEmailActivity.this.runOnUiThread(() -> {
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

    private void updateUiWithUser(String email) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            Intent switchActivityIntent = new Intent(this, ChangePasswordActivity.class);
            switchActivityIntent.putExtra("extras", bundle);
            startActivity(switchActivityIntent);
        } catch (Exception e) {
            Log.e("TAG", "updateUiWithUser: ", e);
        }
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
