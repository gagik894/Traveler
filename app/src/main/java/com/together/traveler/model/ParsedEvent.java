package com.together.traveler.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class ParsedEvent implements Parcelable {
    private String title;
    private String location;
    private String image;
    private String description;
    private String duration;
    private String category;
    private String min_age;
    private String username;
    private String userAvatar;
    private String startDate;
    private String endDate;
    private double longitude;
    private double latitude;
    private ArrayList<Integer> prices;
    private ArrayList<String> tags;
    private ArrayList<String> more_images;
    private ArrayList<Date> dates;
    private GeoPoint geoPoint;
    private String url;

    private boolean liked = false;


    public ParsedEvent(String name, String location, String image, String description, String duration, String category, String min_age, ArrayList<Integer> prices, ArrayList<String> tags, ArrayList<String> more_images, ArrayList<Date> dates, String link, GeoPoint geoPoint, String username, String userAvatar) {
        this.title = name;
        this.location = location;
        this.image = image;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.min_age = min_age;
        this.prices = prices;
        this.tags = tags;
        this.more_images = more_images;
        this.dates = dates;
        this.url = link;
        this.geoPoint = geoPoint;
        this.username = username;
        this.userAvatar = userAvatar;
    }


    public ParsedEvent(){}


    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMin_age() {
        return min_age;
    }

    public void setMin_age(String min_age) {
        this.min_age = min_age;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<Integer> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Integer> prices) {
        this.prices = prices;
    }

    public ArrayList<String> getMore_images() {
        return more_images;
    }

    public void setMore_images(ArrayList<String> more_images) {
        this.more_images = more_images;
    }


    public ArrayList<Date> getDates() {
        return dates;
    }

    public void setDates(ArrayList<Date> dates) {
        this.dates = dates;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeString(this.image);
        dest.writeString(this.description);
        dest.writeString(this.duration);
        dest.writeString(this.category);
        dest.writeString(this.min_age);
        dest.writeString(this.username);
        dest.writeString(this.userAvatar);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeList(this.prices);
        dest.writeStringList(this.tags);
        dest.writeStringList(this.more_images);
        dest.writeList(this.dates);
        dest.writeParcelable(this.geoPoint, flags);
        dest.writeString(this.url);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.location = source.readString();
        this.image = source.readString();
        this.description = source.readString();
        this.duration = source.readString();
        this.category = source.readString();
        this.min_age = source.readString();
        this.username = source.readString();
        this.userAvatar = source.readString();
        this.startDate = source.readString();
        this.endDate = source.readString();
        this.longitude = source.readDouble();
        this.latitude = source.readDouble();
        this.prices = new ArrayList<Integer>();
        source.readList(this.prices, Integer.class.getClassLoader());
        this.tags = source.createStringArrayList();
        this.more_images = source.createStringArrayList();
        this.dates = new ArrayList<Date>();
        source.readList(this.dates, Date.class.getClassLoader());
        this.geoPoint = source.readParcelable(GeoPoint.class.getClassLoader());
        this.url = source.readString();
        this.liked = source.readByte() != 0;
    }

    protected ParsedEvent(Parcel in) {
        this.title = in.readString();
        this.location = in.readString();
        this.image = in.readString();
        this.description = in.readString();
        this.duration = in.readString();
        this.category = in.readString();
        this.min_age = in.readString();
        this.username = in.readString();
        this.userAvatar = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.prices = new ArrayList<Integer>();
        in.readList(this.prices, Integer.class.getClassLoader());
        this.tags = in.createStringArrayList();
        this.more_images = in.createStringArrayList();
        this.dates = new ArrayList<Date>();
        in.readList(this.dates, Date.class.getClassLoader());
        this.geoPoint = in.readParcelable(GeoPoint.class.getClassLoader());
        this.url = in.readString();
        this.liked = in.readByte() != 0;
    }

    public static final Creator<ParsedEvent> CREATOR = new Creator<ParsedEvent>() {
        @Override
        public ParsedEvent createFromParcel(Parcel source) {
            return new ParsedEvent(source);
        }

        @Override
        public ParsedEvent[] newArray(int size) {
            return new ParsedEvent[size];
        }
    };
}
