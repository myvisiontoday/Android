package com.myvisontoday.ourmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rabin on 07/06/2017.
 */

public class User {
//Instance variables
    private String userUID;
    private String userName;
    private String age;
    private String gender;
    private LatLng latlng;
    private float distance;

    //Properties
    public String getUserID() {
        return userUID;
    }
    public void setUserID(String userID) {
        this.userUID = userID;
    }

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getAge () {return age;}
    public void setAge(String age) {this.age = age;}

    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}

    public LatLng getLatLng() {return latlng;}
    public void setLatLng(LatLng latlng) {this.latlng = latlng;}



    //Constructor
    public User()
    {

    }
}
