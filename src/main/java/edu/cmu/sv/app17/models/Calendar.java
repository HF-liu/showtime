package edu.cmu.sv.app17.models;

public class Calendar {
    String calendarId = null;
    String userId;
    String calList;
    String showList;

    public Calendar(String userId, String calList, String showList) {
        this.userId = userId;
        this.calList = calList;
        this.showList = showList;
    }

    public void setId(String id) {
        this.calendarId = id;
    }
}
