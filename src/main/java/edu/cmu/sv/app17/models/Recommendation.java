package edu.cmu.sv.app17.models;

public class Recommendation {
    String recId = null;
    String userId;
    String showId;

    public Recommendation(String userId, String showId) {
        this.userId = userId;
        this.showId = showId;
    }

    public void setId(String id) {
        this.recId = id;
    }
}
