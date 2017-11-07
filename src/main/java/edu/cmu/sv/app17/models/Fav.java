package edu.cmu.sv.app17.models;

public class Fav {
    String favId = null;
    String userId;
    String showId;

    public Fav(String userId, String showId) {
        this.userId = userId;
        this.showId = showId;
    }

    public void setId(String id) {
        this.favId = id;
    }
}
