package com.together.traveler.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer repeatPasswordError;
    @Nullable
    private Integer secCodeError;
    private final boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer repeatPasswordError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.repeatPasswordError = repeatPasswordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.repeatPasswordError = null;
        this.secCodeError = null;
        this.isDataValid = isDataValid;
    }

    LoginFormState(@Nullable Integer secCodeError) {
        this.secCodeError = secCodeError;
        this.isDataValid = false;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }
    @Nullable
    Integer getEmailError() {
        return emailError;
    }
    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }
    @Nullable
    Integer getRepeatPasswordError() {
        return repeatPasswordError;
    }

    @Nullable
    public Integer getSecCodeError() {
        return secCodeError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}