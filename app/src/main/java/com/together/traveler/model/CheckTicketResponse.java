package com.together.traveler.model;

public class CheckTicketResponse {
    private boolean enrolled;
    private User User;

    public CheckTicketResponse(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

}
