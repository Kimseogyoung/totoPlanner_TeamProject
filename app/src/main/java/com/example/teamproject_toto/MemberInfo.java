package com.example.teamproject_toto;

import java.util.ArrayList;

public class MemberInfo {
    private String icon;
    private String name;
    private String id;
    private String phoneNumber;
    private ArrayList<String> friends;
    private String friendcode;

    public MemberInfo(String icon,String name,String id,String phoneNumber ,String friendcode){
        this.icon=icon;
        this.name=name;
        this.id=id;
        this.phoneNumber=phoneNumber;
        this.friendcode=friendcode;
        friends=new ArrayList<String>();

    }
    public String getIcon(){return icon;}
    public void setIcon(String icon){this.icon=icon;}

    public String getName(){return name;}
    public void setName(String n){name=n;}

    public void setId(String email) {
        this.id = email;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFriendcode(String friendcode) {
        this.friendcode = friendcode;
    }

    public String getFriendcode() {
        return friendcode;
    }

}