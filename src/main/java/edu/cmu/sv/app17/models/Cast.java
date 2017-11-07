package edu.cmu.sv.app17.models;

public class Cast {
    String castId = null;
    String showId;
    String castName;
    String roles;
    String castPhoto;

    public Cast(String showId, String castName, String roles, String castPhoto) {
        this.showId = showId;
        this.castName = castName;
        this.roles = roles;
        this.castPhoto = castPhoto;
    }

    public void setId(String id) {
        this.castId = id;
    }
}

