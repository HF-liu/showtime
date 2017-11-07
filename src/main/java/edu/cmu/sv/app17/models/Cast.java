package edu.cmu.sv.app17.models;

public class Cast {
    String castId = null;
    String showId;
    String castName;
    String roles;

    public Cast(String castName, String showId, String roles, String castPhoto, String castNews) {
        this.showId = showId;
        this.castName = castName;
        this.roles = roles;
    }
    public void setId(String id) {
        this.castId = id;
    }
}
