package com.together.traveler.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Card implements Parcelable {
    private String name;
    private String location;
    private LocalDate date;
    private LocalTime time;

    public Card(String name, String location, LocalDate date, LocalTime time) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }


    private static int lastId = 0;

    public static ArrayList<Card> createCardList(int numContacts) {
        lastId = 0;
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 1; i <= numContacts; i++) {
            cards.add(new Card("Event " + ++lastId + " hrth gx sofah fasds", "Somewhere", LocalDate.now(), LocalTime.now()));
        }

        return cards;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeSerializable(this.date);
        dest.writeSerializable(this.time);
    }


    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.location = source.readString();
        this.date = (LocalDate) source.readSerializable();
        this.time = (LocalTime) source.readSerializable();
    }

    protected Card(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.date = (LocalDate) in.readSerializable();
        this.time = (LocalTime) in.readSerializable();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
