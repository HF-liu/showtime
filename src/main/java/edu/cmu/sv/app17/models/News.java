package edu.cmu.sv.app17.models;

import java.util.Date;

public class News {
    String newId = null;
    String source;
    Date date;
    String title;
    String content;

    public News(String source, Date date, String title, String content) {
        this.source = source;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public void setId(String id) {
        this.newId = id;
    }
}
