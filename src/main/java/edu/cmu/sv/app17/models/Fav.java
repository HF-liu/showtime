package edu.cmu.sv.app17.models;

public class Fav {
    String favId = null;
    String userId;
    String showId;
    String favShows;
    String showPics;
    String latestWatched;
    String castNews;

    public Fav(String userId, String showId, String favShows, String showPics, String latestWatched, String castNews) {
        this.userId = userId;
        this.showId = showId;
        this.favShows = favShows;
        this.showPics = showPics;
        this.latestWatched = latestWatched;
        this.castNews = castNews;
    }

    public void setId(String id) {
        this.favId = id;
    }
}
