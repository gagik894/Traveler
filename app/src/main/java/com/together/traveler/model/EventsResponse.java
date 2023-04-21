package com.together.traveler.model;

import java.util.List;

public class EventsResponse {
    private List<Event> data;
    private String userId;

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}