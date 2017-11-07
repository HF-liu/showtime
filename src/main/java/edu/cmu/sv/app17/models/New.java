package edu.cmu.sv.app17.models;

public class New {
    String newId = null;
    String source;
    String date;
    String title;
    String content;
    Number likes;
    String relatedShow;
    String relatedChan;
    String relatedCast;

    public New(String source, String date, String title, String content, Number likes, String relatedShow, String relatedChan, String relatedCast) {
        this.source = source;
        this.date = date;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.relatedShow = relatedShow;
        this.relatedChan = relatedChan;
        this.relatedCast = relatedCast;
    }

    public void setId(String id) {
        this.newId = id;
    }
}
