package edu.cmu.sv.app17.models;

public class Admin{
    String adminId = null;
    String userId;

    public Admin(String userId) {
        this.userId = userId;
    }
    public void setId(String id) {
        this.adminId = id;
    }
}
