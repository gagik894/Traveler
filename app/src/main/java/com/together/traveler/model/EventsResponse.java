package com.together.traveler.model;

import java.util.List;

public class EventsResponse {
    private List<Event> data;
    private String user;

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}