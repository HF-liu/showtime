package edu.cmu.sv.app17.models;

public class Admin{
    String userId = null;
    String adminId;

    public Admin(String userId) {
        this.userId = userId;
    }
    public void setId(String id) {
        this.adminId = id;
    }
}
