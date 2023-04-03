package com.together.traveler.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.together.traveler.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event implements Parcelable {
    private String name;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private int image;
    private String description;
    private boolean enrolled;
    private boolean saved;
    private User by;

    public Event(String name, String location, LocalDate date, LocalTime time, int image, String description, User by, boolean enrolled, boolean saved) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.image = image;
        this.description = description;
        this.enrolled = enrolled;
        this.saved = saved;
        this.by = by;

    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void enroll(){
        enrolled = true;
    }

    public boolean isSaved() {
        return saved;
    }

    public void save(){
        this.saved = !this.saved;
    }
    public User getUser() {
        return by;
    }


    public static ArrayList<Event> createCardList(int quantity) {
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Quis ipsum suspendisse ultrices gravida dictum fusce. Vel eros donec ac odio tempor orci dapibus ultrices in. Urna nec tincidunt praesent semper feugiat. Quis eleifend quam adipiscing vitae. Mi tempus imperdiet nulla malesuada pellentesque. Vitae auctor eu augue ut. Eu volutpat odio facilisis mauris sit amet massa vitae tortor. Ultrices gravida dictum fusce ut placerat orci nulla pellentesque dignissim. Fermentum iaculis eu non diam phasellus vestibulum lorem sed risus. Sed id semper risus in hendrerit gravida. In vitae turpis massa sed. Tortor dignissim convallis aenean et tortor at. Turpis egestas maecenas pharetra convallis. A cras semper auctor neque vitae. Aliquam ut porttitor leo a. Lacinia quis vel eros donec ac odio tempor orci dapibus.";
        int lastId = 0;
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            events.add(new Event("Event " + ++lastId, "somewhere", LocalDate.now(), LocalTime.now(),R.drawable.event, i%2 == 0?"description": longDesc, new User("username", "somewhere", 4.8f, R.drawable.default_user), false, false));
        }
        return events;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeSerializable(this.date);
        dest.writeSerializable(this.time);
        dest.writeInt(this.image);
        dest.writeString(this.description);
        dest.writeByte(this.enrolled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.saved ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.by, flags);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.location = source.readString();
        this.date = (LocalDate) source.readSerializable();
        this.time = (LocalTime) source.readSerializable();
        this.image = source.readInt();
        this.description = source.readString();
        this.enrolled = source.readByte() != 0;
        this.saved = source.readByte() != 0;
        this.by = source.readParcelable(User.class.getClassLoader());
    }

    protected Event(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.date = (LocalDate) in.readSerializable();
        this.time = (LocalTime) in.readSerializable();
        this.image = in.readInt();
        this.description = in.readString();
        this.enrolled = in.readByte() != 0;
        this.saved = in.readByte() != 0;
        this.by = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
