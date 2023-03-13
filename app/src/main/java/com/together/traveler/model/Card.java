package com.together.traveler.model;

import android.location.Location;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Card {
    private final String name;
    private final Location location;
    private final LocalDate date;
    private final LocalTime time;

    public Card(String name, Location location, LocalDate date, LocalTime time) {
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
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 1; i <= numContacts; i++) {
            cards.add(new Card("Event " + ++lastId, null, LocalDate.now(), LocalTime.now()));
        }

        return cards;
    }

    public Location getLocation() {
        return location;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}
