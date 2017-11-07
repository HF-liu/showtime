package edu.cmu.sv.app17.models;

public class User {
    String userId = null;
    String userName;
    String email;

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }
    public void setId(String id) {
        this.userId = id;
    }
}
