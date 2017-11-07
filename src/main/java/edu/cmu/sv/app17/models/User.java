package edu.cmu.sv.app17.models;

public class User {
    String userId = null;
    String userName;
    String email;
    String password;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
    public void setId(String id) {
        this.userId = id;
    }
}
