package com.together.traveler.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String name;
    private String location;
    private String imgId;
    private String _id;
    private String description;
    private String phone;
    private String url;
    private Double longitude;
    private Double latitude;
    private float rating;
    private Bitmap imageBitmap;
    private String[] openingTimes;
    private String[] closingTimes;
    private boolean[] isClosedDays;
    private boolean userOwned;
    private boolean alwaysOpen;
    private User userId;
    private String category;

    public Place(){
        this.name = "";
        this.location = "";
        this.latitude = null;
        this.longitude = null;
        this.description = "";
        this.imageBitmap = null;
        this.url = "";
        this.phone = "";
        this.openingTimes = new String[7];
        this.closingTimes = new String[7];
        this.isClosedDays = new boolean[7];
        this.alwaysOpen = false;
        this.category = "";
        this.imgId = "";
        this._id = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String[] getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(String[] openingTimes) {
        this.openingTimes = openingTimes;
    }

    public String[] getClosingTimes() {
        return closingTimes;
    }

    public void setClosingTimes(String[] closingTimes) {
        this.closingTimes = closingTimes;
    }

    public boolean[] getIsClosedDays() {
        return isClosedDays;
    }

    public void setIsClosedDays(boolean[] isClosedDays) {
        this.isClosedDays = isClosedDays;
    }

    public boolean isUserOwned() {
        return userOwned;
    }

    public void setUserOwned(boolean userOwned) {
        this.userOwned = userOwned;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public void setAlwaysOpen(boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeString(this.imgId);
        dest.writeString(this._id);
        dest.writeString(this.description);
        dest.writeString(this.phone);
        dest.writeString(this.url);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeFloat(this.rating);
        dest.writeParcelable(this.imageBitmap, flags);
        dest.writeStringArray(this.openingTimes);
        dest.writeStringArray(this.closingTimes);
        dest.writeBooleanArray(this.isClosedDays);
        dest.writeByte(this.userOwned ? (byte) 1 : (byte) 0);
        dest.writeByte(this.alwaysOpen ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.userId, flags);
        dest.writeString(this.category);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.location = source.readString();
        this.imgId = source.readString();
        this._id = source.readString();
        this.description = source.readString();
        this.phone = source.readString();
        this.url = source.readString();
        this.longitude = (Double) source.readValue(Double.class.getClassLoader());
        this.latitude = (Double) source.readValue(Double.class.getClassLoader());
        this.rating = source.readFloat();
        this.imageBitmap = source.readParcelable(Bitmap.class.getClassLoader());
        this.openingTimes = source.createStringArray();
        this.closingTimes = source.createStringArray();
        this.isClosedDays = source.createBooleanArray();
        this.userOwned = source.readByte() != 0;
        this.alwaysOpen = source.readByte() != 0;
        this.userId = source.readParcelable(User.class.getClassLoader());
        this.category = source.readString();
    }

    protected Place(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.imgId = in.readString();
        this._id = in.readString();
        this.description = in.readString();
        this.phone = in.readString();
        this.url = in.readString();
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.rating = in.readFloat();
        this.imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.openingTimes = in.createStringArray();
        this.closingTimes = in.createStringArray();
        this.isClosedDays = in.createBooleanArray();
        this.userOwned = in.readByte() != 0;
        this.alwaysOpen = in.readByte() != 0;
        this.userId = in.readParcelable(User.class.getClassLoader());
        this.category = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
