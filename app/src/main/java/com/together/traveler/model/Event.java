package com.together.traveler.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.together.traveler.R;

import java.util.ArrayList;

public class Event implements Parcelable {
    private String title;
    private String location;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String imgId;
    private String description;
    private int ticketsCount;
    private Bitmap imageBitmap;
    private boolean enrolled;
    private boolean saved;
    private User userId;

    public Event(String title, String location, String startDate, String startTime, String endDate, String endTime, String imgId, String description, User userId, int ticketsCount) {
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.imgId = imgId;
        this.description = description;
        this.userId = userId;
        this.ticketsCount = ticketsCount;
    }

    public Event(){
        this.title = "";
        this.location = "";
        this.startDate = "";
        this.startTime = "";
        this.endDate = "";
        this.endTime = "";
        this.description = "";
        this.imageBitmap = null;
        this.ticketsCount = 0;
        this.imgId = "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTicketsCount(int ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }


    public void enroll(){
        enrolled = true;
    }

    public void save(){
        this.saved = !this.saved;
    }


    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getImgId() {
        return imgId;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public User getUser() {
        return userId;
    }

    public int getTicketsCount() {
        return ticketsCount;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public boolean isSaved() {
        return saved;
    }

    public static ArrayList<Event> createCardList(int quantity) {
        String longDesc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Quis ipsum suspendisse ultrices gravida dictum fusce. Vel eros donec ac odio tempor orci dapibus ultrices in. Urna nec tincidunt praesent semper feugiat. Quis eleifend quam adipiscing vitae. Mi tempus imperdiet nulla malesuada pellentesque. Vitae auctor eu augue ut. Eu volutpat odio facilisis mauris sit amet massa vitae tortor. Ultrices gravida dictum fusce ut placerat orci nulla pellentesque dignissim. Fermentum iaculis eu non diam phasellus vestibulum lorem sed risus. Sed id semper risus in hendrerit gravida. In vitae turpis massa sed. Tortor dignissim convallis aenean et tortor at. Turpis egestas maecenas pharetra convallis. A cras semper auctor neque vitae. Aliquam ut porttitor leo a. Lacinia quis vel eros donec ac odio tempor orci dapibus.";
        int lastId = 0;
        String url;
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            url = "1JXGfBOuLtEpTollfniq_fWhqfESFdOxl";
            events.add(new Event("Event " + ++lastId, "somewhere", "Mon, 5 April",
                    "18:00", "Mon, 5 Apr", "20:00", url,
                    i%2 == 0?"description": longDesc,
                    new User("username", "somewhere", 4.8f, R.drawable.default_user),
                    25));
        }
        return events;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeString(this.startDate);
        dest.writeString(this.startTime);
        dest.writeString(this.endDate);
        dest.writeString(this.endTime);
        dest.writeString(this.imgId);
        dest.writeString(this.description);
        dest.writeInt(this.ticketsCount);
        dest.writeParcelable(this.imageBitmap, flags);
        dest.writeByte(this.enrolled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.saved ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.userId, flags);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.location = source.readString();
        this.startDate = source.readString();
        this.startTime = source.readString();
        this.endDate = source.readString();
        this.endTime = source.readString();
        this.imgId = source.readString();
        this.description = source.readString();
        this.ticketsCount = source.readInt();
        this.imageBitmap = source.readParcelable(Bitmap.class.getClassLoader());
        this.enrolled = source.readByte() != 0;
        this.saved = source.readByte() != 0;
        this.userId = source.readParcelable(User.class.getClassLoader());
    }

    protected Event(Parcel in) {
        this.title = in.readString();
        this.location = in.readString();
        this.startDate = in.readString();
        this.startTime = in.readString();
        this.endDate = in.readString();
        this.endTime = in.readString();
        this.imgId = in.readString();
        this.description = in.readString();
        this.ticketsCount = in.readInt();
        this.imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.enrolled = in.readByte() != 0;
        this.saved = in.readByte() != 0;
        this.userId = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
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
