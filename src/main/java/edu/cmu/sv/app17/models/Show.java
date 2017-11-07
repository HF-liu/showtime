package edu.cmu.sv.app17.models;

public class Show {
     String showId = null;
     String showName;
     String channelId;
     String intro;
     String showCategory;
     String showphoto;
     Integer showRating;

    public Show(String showName, String channelId, String intro, String showCategory, String showphoto, Integer showRating) {
        this.showName = showName;
        this.channelId = channelId;
        this.intro = intro;
        this.showCategory = showCategory;
        this.showphoto = showphoto;
        this.showRating = showRating;
    }

    public void setId(String id) {
        this.showId = id;
    }
}
