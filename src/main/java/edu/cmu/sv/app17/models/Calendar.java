package edu.cmu.sv.app17.models;

import java.util.Date;

public class Calendar {
    String calendarId = null;
    String userId;
    Date date;
    String event;

    public Calendar(String userId, Date date, String event) {
        this.userId = userId;
        this.date = date;
        this.event = event;
    }

    public void setId(String id) {
        this.calendarId = id;
    }
}
