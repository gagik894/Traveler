package com.together.traveler.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String avatar;
    private String email;
    private String username;
    private String _id;
    private String location;
    private String password;
    private String FCMToken;
    private float rating;
    private ArrayList<Event> upcomingEvents = null;
    private ArrayList<Event> savedEvents = null;
    private ArrayList<Event> userEvents = null;
    private boolean admin = false;

    public User(String avatar, String username, String location, String password, float rating, ArrayList<Event> upcomingEvents, ArrayList<Event> savedEvents, ArrayList<Event> userEvents) {
        this.avatar = avatar;
        this.username = username;
        this.location = location;
        this.password = password;
        this.rating = rating;
        this.upcomingEvents = upcomingEvents;
        this.savedEvents = savedEvents;
        this.userEvents = userEvents;
    }

    public User(String avatar, String username, String location, float rating) {
        this.avatar = avatar;
        this.username = username;
        this.location = location;
        this.rating = rating;
    }

    public User(String username, String email, String password, String FCMToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.FCMToken = FCMToken;
    }

    public User(String _id, String email) {
        this.email = email;
        this._id = _id;
    }

    public User() {

    }

    public String toString() {
        return "User{" +
                "avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", id='" + _id + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                ", upcomingEvents=" + upcomingEvents +
                ", savedEvents=" + savedEvents +
                ", userEvents=" + userEvents +
                ", admin=" + admin +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public String getPassword() {
        return password;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public float getRating() {
        return rating;
    }

    public String getAvatar() {
        return avatar;
    }

    public ArrayList<Event> getUpcomingEvents() {
        return upcomingEvents;
    }

    public ArrayList<Event> getSavedEvents() {
        return savedEvents;
    }

    public ArrayList<Event> getUserEvents() {
        return userEvents;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.email);
        dest.writeString(this.username);
        dest.writeString(this._id);
        dest.writeString(this.location);
        dest.writeString(this.password);
        dest.writeFloat(this.rating);
        dest.writeTypedList(this.upcomingEvents);
        dest.writeTypedList(this.savedEvents);
        dest.writeTypedList(this.userEvents);
        dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.avatar = source.readString();
        this.email = source.readString();
        this.username = source.readString();
        this._id = source.readString();
        this.location = source.readString();
        this.password = source.readString();
        this.rating = source.readFloat();
        this.upcomingEvents = source.createTypedArrayList(Event.CREATOR);
        this.savedEvents = source.createTypedArrayList(Event.CREATOR);
        this.userEvents = source.createTypedArrayList(Event.CREATOR);
        this.admin = source.readByte() != 0;
    }

    protected User(Parcel in) {
        this.avatar = in.readString();
        this.email = in.readString();
        this.username = in.readString();
        this._id = in.readString();
        this.location = in.readString();
        this.password = in.readString();
        this.rating = in.readFloat();
        this.upcomingEvents = in.createTypedArrayList(Event.CREATOR);
        this.savedEvents = in.createTypedArrayList(Event.CREATOR);
        this.userEvents = in.createTypedArrayList(Event.CREATOR);
        this.admin = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
