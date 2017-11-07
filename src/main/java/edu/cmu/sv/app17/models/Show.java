package edu.cmu.sv.app17.models;


public class Show {
     String showId = null;
     String showName;
     Number seasons;
     String channel;
     String intro;
     String cast;
     Double showRating;
     String showPhoto;
     String showTime;
     String nextEpisode;
     String showCategory;
     String reviews;
     Number showFollow;

    public Show(String showName, Number seasons, String channel, String intro,
                String cast, Double showRating, String showPhoto, String showTime,
                String nextEpisode, String showCategory, String reviews, Number showFollow) {
        this.showName = showName;
        this.seasons = seasons;
        this.channel = channel;
        this.intro = intro;
        this.cast = cast;
        this.showRating = showRating;
        this.showPhoto = showPhoto;
        this.showTime = showTime;
        this.nextEpisode = nextEpisode;
        this.showCategory = showCategory;
        this.reviews = reviews;
        this.showFollow = showFollow;
    }

    public void setId(String id) {
        this.showId = id;
    }
}
