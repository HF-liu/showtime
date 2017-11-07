package edu.cmu.sv.app17.models;

public class Channel {
    String channelId = null;
    String channelName;
    String channelLogo;
    String showList;
    Number showNumber;

    public Channel(String channelName, String channelLogo, String showList, Number showNumber) {
        this.channelName = channelName;
        this.channelLogo = channelLogo;
        this.showList = showList;
        this.showNumber = showNumber;
    }


    public void setId(String id) {
        this.channelId = id;
    }
}
