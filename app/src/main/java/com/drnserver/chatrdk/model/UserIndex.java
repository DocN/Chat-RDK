package com.drnserver.chatrdk.model;

/**
 * Created by steve on 2/26/2018.
 */

//A class to hold all the value needed to search the users. - Steven
public class UserIndex {

    private String email;
    private String image;
    private String nameIndex;
    private String phone;
    private String status;
    private String UID;

    public UserIndex(){}

    public UserIndex(String userEmail, String userImage, String userName,  String userPhone,
                     String userStatus, String UID) {
        email = userEmail;
        image = userImage;
        nameIndex = userName;
        phone = userPhone;
        status = userStatus;
        this.UID = UID;
    }

    public String getUserSearchEmail() {return email;}
    public String getUserSearchImage(){return image;}
    public String getUserSearchName() {return nameIndex;}
    public String getUserSearchPhone(){return phone;}
    public String getUserSearchStatus(){return status;}
    public String getUserSearchUID(){return UID;}

    public void setUserSearchName(String s) {nameIndex = s;}
    public void setUserSearchEmail(String s) {email = s;}
    public void setUserSearchPhone(String s){phone = s;}
    public void setUserSearchImage(String s){image = s;}
    public void setUserSearchStatus(String s){status = s;}
    public void setUserSearchUID(String s){UID = s;}
}
