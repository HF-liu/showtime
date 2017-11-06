package edu.cmu.sv.app17.models;

import edu.cmu.sv.app17.helpers.APPCrypt;

public class Session {

    String token = null;
    String userId = null;
    String userName = null;

    public Session(User user) throws Exception{
        this.userId = user.userId;
        this.token = APPCrypt.encrypt(user.userId);
        this.userName = user.userName;
    }
}
