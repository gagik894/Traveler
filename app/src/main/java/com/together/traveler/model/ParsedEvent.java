package com.together.traveler.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class ParsedEvent implements Parcelable {
    private String title;
    private String location;
    private String img_url;
    private String description;
    private String duration;
    private String genre;
    private String min_age;
    private String username;
    private String userAvatar;
    private String startDate;
    private String endDate;
    private ArrayList<Integer> prices;
    private ArrayList<String> tags;
    private ArrayList<String> more_images;
    private ArrayList<Date> dates;
    private GeoPoint geoPoint;
    private String link;

    private boolean liked = false;


    public ParsedEvent(String name, String location, String img_url, String description, String duration, String genre, String min_age, ArrayList<Integer> prices, ArrayList<String> tags, ArrayList<String> more_images, ArrayList<Date> dates, String link, GeoPoint geoPoint, String username, String userAvatar) {
        this.title = name;
        this.location = location;
        this.img_url = img_url;
        this.description = description;
        this.duration = duration;
        this.genre = genre;
        this.min_age = min_age;
        this.prices = prices;
        this.tags = tags;
        this.more_images = more_images;
        this.dates = dates;
        this.link = link;
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeString(this.img_url);
        dest.writeString(this.description);
        dest.writeString(this.duration);
        dest.writeString(this.genre);
        dest.writeString(this.min_age);
        dest.writeString(this.username);
        dest.writeString(this.userAvatar);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeList(this.prices);
        dest.writeStringList(this.tags);
        dest.writeStringList(this.more_images);
        dest.writeList(this.dates);
        dest.writeParcelable(this.geoPoint, flags);
        dest.writeString(this.link);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.location = source.readString();
        this.img_url = source.readString();
        this.description = source.readString();
        this.duration = source.readString();
        this.genre = source.readString();
        this.min_age = source.readString();
        this.username = source.readString();
        this.userAvatar = source.readString();
        this.startDate = source.readString();
        this.endDate = source.readString();
        this.prices = new ArrayList<Integer>();
        source.readList(this.prices, Integer.class.getClassLoader());
        this.tags = source.createStringArrayList();
        this.more_images = source.createStringArrayList();
        this.dates = new ArrayList<Date>();
        source.readList(this.dates, Date.class.getClassLoader());
        this.geoPoint = source.readParcelable(GeoPoint.class.getClassLoader());
        this.link = source.readString();
        this.liked = source.readByte() != 0;
    }

    protected ParsedEvent(Parcel in) {
        this.title = in.readString();
        this.location = in.readString();
        this.img_url = in.readString();
        this.description = in.readString();
        this.duration = in.readString();
        this.genre = in.readString();
        this.min_age = in.readString();
        this.username = in.readString();
        this.userAvatar = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.prices = new ArrayList<Integer>();
        in.readList(this.prices, Integer.class.getClassLoader());
        this.tags = in.createStringArrayList();
        this.more_images = in.createStringArrayList();
        this.dates = new ArrayList<Date>();
        in.readList(this.dates, Date.class.getClassLoader());
        this.geoPoint = in.readParcelable(GeoPoint.class.getClassLoader());
        this.link = in.readString();
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
