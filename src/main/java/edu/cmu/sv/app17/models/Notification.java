package edu.cmu.sv.app17.models;

public class Notification {
    String notiId = null;
    String showId;
    String favShows;
    String showPicture;
    String latestWatched;

    public Notification(String showId, String favShows, String showPicture, String latestWatched) {
        this.showId = showId;
        this.favShows = favShows;
        this.showPicture = showPicture;
        this.latestWatched = latestWatched;
    }

    public void setId(String id) {
        this.notiId = id;
    }
}
