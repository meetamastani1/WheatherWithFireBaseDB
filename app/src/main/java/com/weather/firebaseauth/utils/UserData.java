package com.weather.firebaseauth.utils;

/**
 * Created by Techpass Master on 01-Jul-20.
 * www.techpassmaster.com
 */

public class UserData {
    public String userName, emailId, profilePic,bio;

    public UserData(){
    }
    public UserData(String userName, String emailId, String profilePic, String bio) {
        this.userName = userName;
        this.emailId = emailId;
        this.profilePic = profilePic;
        this.bio =bio;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userName='" + userName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}