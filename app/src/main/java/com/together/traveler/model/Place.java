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
    private Double longitude;
    private Double latitude;
    private Bitmap imageBitmap;
    private boolean userOwned;
    private User userId;
    private String category;

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

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public boolean isUserOwned() {
        return userOwned;
    }

    public void setUserOwned(boolean userOwned) {
        this.userOwned = userOwned;
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
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeParcelable(this.imageBitmap, flags);
        dest.writeByte(this.userOwned ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.userId, flags);
        dest.writeString(this.category);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.location = source.readString();
        this.imgId = source.readString();
        this._id = source.readString();
        this.description = source.readString();
        this.longitude = (Double) source.readValue(Double.class.getClassLoader());
        this.latitude = (Double) source.readValue(Double.class.getClassLoader());
        this.imageBitmap = source.readParcelable(Bitmap.class.getClassLoader());
        this.userOwned = source.readByte() != 0;
        this.userId = source.readParcelable(User.class.getClassLoader());
        this.category = source.readString();
    }

    public Place() {
    }

    protected Place(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.imgId = in.readString();
        this._id = in.readString();
        this.description = in.readString();
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.userOwned = in.readByte() != 0;
        this.userId = in.readParcelable(User.class.getClassLoader());
        this.category = in.readString();
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
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
