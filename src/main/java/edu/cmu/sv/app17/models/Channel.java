package edu.cmu.sv.app17.models;

public class Channel {
    String channelId = null;
    String channelName;
    String channelLogo;

    public Channel(String channelName, String channelLogo) {
        this.channelName = channelName;
        this.channelLogo = channelLogo;
    }


    public void setId(String id) {
        this.channelId = id;
    }
}
