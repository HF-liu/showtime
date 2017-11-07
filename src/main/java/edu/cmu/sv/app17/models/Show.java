package edu.cmu.sv.app17.models;


public class Show {
     String showId = null;
     String showName;
     String channelId;
     String intro;
     String showCategory;

    public Show(String showName, String channelId, String intro, String showCategory,) {
        this.showName = showName;
        this.channelId = channelId;
        this.intro = intro;
        this.showCategory = showCategory;
    }

    public void setId(String id) {
        this.showId = id;
    }
}
