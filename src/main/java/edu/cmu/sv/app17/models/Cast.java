package edu.cmu.sv.app17.models;

public class Cast {
    String castId = null;
    String castName;
    String roles;
    String castPhoto;
    String castNews;

    public Cast(String castName, String roles, String castPhoto, String castNews) {
        this.castName = castName;
        this.roles = roles;
        this.castPhoto = castPhoto;
        this.castNews = castNews;
    }
    public void setId(String id) {
        this.castId = id;
    }
}
