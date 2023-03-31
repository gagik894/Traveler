package com.together.traveler.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

public class User implements Parcelable {
    private String email;
    private String username;
    private String userId;
    private String location;
    private float rating;
    private int profileImage;
    private ArrayList<Event> upcomingEvents = null;
    private ArrayList<Event> savedEvents = null;
    private ArrayList<Event> userEvents = null;

    public User(String username, String location, float rating, int profileImage, ArrayList<Event> upcomingEvents, ArrayList<Event> savedEvents, ArrayList<Event> userEvents) {
        this.username = username;
        this.location = location;
        this.rating = rating;
        this.profileImage = profileImage;
        this.upcomingEvents = upcomingEvents;
        this.savedEvents = savedEvents;
        this.userEvents = userEvents;
    }

    public User(String username, String location, float rating, int profileImage) {
        this.username = username;
        this.location = location;
        this.rating = rating;
        this.profileImage = profileImage;
    }

    public User(String userId, String email, String username) {
        this.email = email;
        this.username = username;
        this.userId = userId;
    }

    public User(String userId, String email) {
        this.email = email;
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
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

    public float getRating() {
        return rating;
    }

    public int getProfileImage() {
        return profileImage;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.username);
        dest.writeString(this.userId);
        dest.writeString(this.location);
        dest.writeFloat(this.rating);
        dest.writeInt(this.profileImage);
        dest.writeTypedList(this.upcomingEvents);
        dest.writeTypedList(this.savedEvents);
        dest.writeTypedList(this.userEvents);
    }

    public void readFromParcel(Parcel source) {
        this.email = source.readString();
        this.username = source.readString();
        this.userId = source.readString();
        this.location = source.readString();
        this.rating = source.readFloat();
        this.profileImage = source.readInt();
        this.upcomingEvents = source.createTypedArrayList(Event.CREATOR);
        this.savedEvents = source.createTypedArrayList(Event.CREATOR);
        this.userEvents = source.createTypedArrayList(Event.CREATOR);
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.username = in.readString();
        this.userId = in.readString();
        this.location = in.readString();
        this.rating = in.readFloat();
        this.profileImage = in.readInt();
        this.upcomingEvents = in.createTypedArrayList(Event.CREATOR);
        this.savedEvents = in.createTypedArrayList(Event.CREATOR);
        this.userEvents = in.createTypedArrayList(Event.CREATOR);
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
