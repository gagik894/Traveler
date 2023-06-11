package com.together.traveler.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginResponse implements Parcelable {
    String auth_token;
    String error;
    String userId;

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.auth_token);
        dest.writeString(this.error);
        dest.writeString(this.userId);
    }

    public void readFromParcel(Parcel source) {
        this.auth_token = source.readString();
        this.error = source.readString();
        this.userId = source.readString();
    }

    protected LoginResponse(Parcel in) {
        this.auth_token = in.readString();
        this.error = in.readString();
        this.userId = in.readString();
    }

    public static final Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel source) {
            return new LoginResponse(source);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
        }
    };
}
