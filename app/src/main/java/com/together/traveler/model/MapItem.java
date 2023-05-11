package com.together.traveler.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MapItem implements Parcelable {
    private String _id;
    private Double longitude;
    private Double latitude;
    private String category;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
        dest.writeString(this._id);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeString(this.category);
    }

    public void readFromParcel(Parcel source) {
        this._id = source.readString();
        this.longitude = (Double) source.readValue(Double.class.getClassLoader());
        this.latitude = (Double) source.readValue(Double.class.getClassLoader());
        this.category = source.readString();
    }

    public MapItem() {
    }

    protected MapItem(Parcel in) {
        this._id = in.readString();
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.category = in.readString();
    }

    public static final Parcelable.Creator<MapItem> CREATOR = new Parcelable.Creator<MapItem>() {
        @Override
        public MapItem createFromParcel(Parcel source) {
            return new MapItem(source);
        }

        @Override
        public MapItem[] newArray(int size) {
            return new MapItem[size];
        }
    };
}
