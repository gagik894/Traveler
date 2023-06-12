package com.together.traveler.model;

import java.util.List;

public class ParcedResponse {
    private List<ParsedEvent> data;

    public List<ParsedEvent> getData() {
        return data;
    }

    public void setData(List<ParsedEvent> data) {
        this.data = data;
    }
}
