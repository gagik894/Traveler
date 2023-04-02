package com.together.traveler.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String auth_token;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getAuth_token() {
        return auth_token;
    }
}