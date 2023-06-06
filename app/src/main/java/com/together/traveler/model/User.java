package com.together.traveler.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    private String avatar;
    private String email;
    private String username;
    private String _id;
    private String location;
    private String password;
    private String FCMToken;
    private float rating;
    private Map<String, ArrayList<Event>> enrolledEvents = null;
    private Map<String, ArrayList<Event>> favoriteEvents = null;
    private Map<String, ArrayList<Event>> userEvents = null;

//    private ArrayList<Event> upcomingEvents = null;
//    private ArrayList<Event> savedEvents = null;
//    private ArrayList<Event> userEvents = null;
    private boolean admin = false;
    private boolean followed = false;

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
//                ", upcomingEvents=" + upcomingEvents +
//                ", savedEvents=" + savedEvents +
                ", userEvents=" + userEvents +
                ", admin=" + admin +
                ", followed=" + followed +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Map<String, ArrayList<Event>> getEnrolledEvents() {
        return enrolledEvents;
    }

    public void setEnrolledEvents(Map<String, ArrayList<Event>> enrolledEvents) {
        this.enrolledEvents = enrolledEvents;
    }

    public Map<String, ArrayList<Event>> getFavoriteEvents() {
        return favoriteEvents;
    }

    public void setFavoriteEvents(Map<String, ArrayList<Event>> favoriteEvents) {
        this.favoriteEvents = favoriteEvents;
    }

    public Map<String, ArrayList<Event>> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(Map<String, ArrayList<Event>> userEvents) {
        this.userEvents = userEvents;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
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
        dest.writeString(this.FCMToken);
        dest.writeFloat(this.rating);
        dest.writeInt(this.enrolledEvents.size());
        for (Map.Entry<String, ArrayList<Event>> entry : this.enrolledEvents.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
        dest.writeInt(this.favoriteEvents.size());
        for (Map.Entry<String, ArrayList<Event>> entry : this.favoriteEvents.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
        dest.writeInt(this.userEvents.size());
        for (Map.Entry<String, ArrayList<Event>> entry : this.userEvents.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
        dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.followed ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.avatar = source.readString();
        this.email = source.readString();
        this.username = source.readString();
        this._id = source.readString();
        this.location = source.readString();
        this.password = source.readString();
        this.FCMToken = source.readString();
        this.rating = source.readFloat();
        int enrolledEventsSize = source.readInt();
        this.enrolledEvents = new HashMap<String, ArrayList<Event>>(enrolledEventsSize);
        for (int i = 0; i < enrolledEventsSize; i++) {
            String key = source.readString();
            ArrayList<Event> value = source.createTypedArrayList(Event.CREATOR);
            this.enrolledEvents.put(key, value);
        }
        int favoriteEventsSize = source.readInt();
        this.favoriteEvents = new HashMap<String, ArrayList<Event>>(favoriteEventsSize);
        for (int i = 0; i < favoriteEventsSize; i++) {
            String key = source.readString();
            ArrayList<Event> value = source.createTypedArrayList(Event.CREATOR);
            this.favoriteEvents.put(key, value);
        }
        int userEventsSize = source.readInt();
        this.userEvents = new HashMap<String, ArrayList<Event>>(userEventsSize);
        for (int i = 0; i < userEventsSize; i++) {
            String key = source.readString();
            ArrayList<Event> value = source.createTypedArrayList(Event.CREATOR);
            this.userEvents.put(key, value);
        }
        this.admin = source.readByte() != 0;
        this.followed = source.readByte() != 0;
    }

    protected User(Parcel in) {
        this.avatar = in.readString();
        this.email = in.readString();
        this.username = in.readString();
        this._id = in.readString();
        this.location = in.readString();
        this.password = in.readString();
        this.FCMToken = in.readString();
        this.rating = in.readFloat();
        int enrolledEventsSize = in.readInt();
        this.enrolledEvents = new HashMap<String, ArrayList<Event>>(enrolledEventsSize);
        for (int i = 0; i < enrolledEventsSize; i++) {
            String key = in.readString();
            ArrayList<Event> value = in.createTypedArrayList(Event.CREATOR);
            this.enrolledEvents.put(key, value);
        }
        int favoriteEventsSize = in.readInt();
        this.favoriteEvents = new HashMap<String, ArrayList<Event>>(favoriteEventsSize);
        for (int i = 0; i < favoriteEventsSize; i++) {
            String key = in.readString();
            ArrayList<Event> value = in.createTypedArrayList(Event.CREATOR);
            this.favoriteEvents.put(key, value);
        }
        int userEventsSize = in.readInt();
        this.userEvents = new HashMap<String, ArrayList<Event>>(userEventsSize);
        for (int i = 0; i < userEventsSize; i++) {
            String key = in.readString();
            ArrayList<Event> value = in.createTypedArrayList(Event.CREATOR);
            this.userEvents.put(key, value);
        }
        this.admin = in.readByte() != 0;
        this.followed = in.readByte() != 0;
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
