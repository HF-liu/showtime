package edu.cmu.sv.app17.models;

public class User {
    String userId = null;
    String userName;
    String email;
    String phone;
    String profilePhoto;
    String favs;
    Number showNum;
    String reviews;
    String friends;
    String joinDate;

    public User(String userName, String email, String phone,
                String profilePhoto, String favs, Number showNum,
                String reviews, String friends, String joinDate) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.favs = favs;
        this.showNum = showNum;
        this.reviews = reviews;
        this.friends = friends;
        this.joinDate = joinDate;
    }
    public void setId(String id) {
        this.userId = id;
    }
}
