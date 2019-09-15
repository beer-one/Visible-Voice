package com.example.visiblevoice.Data;

public class User {
    private String userID;
    private String deviceToken;

    public User(String userID,String deviceToken) {
            this.userID = userID;
            this.deviceToken = deviceToken;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
