package com.example.teamproject_toto;

import java.util.ArrayList;

public class MemberInfo {
    private int icon;
    private String name;
    private String id;
    private String phoneNumber;
    private ArrayList<String> friends;
    private String friendcord;

    public MemberInfo(int icon,String name,String id,String phoneNumber ,String friendcord){
        this.icon=icon;
        this.name=name;
        this.id=id;
        this.phoneNumber=phoneNumber;
        this.friendcord=friendcord;
        friends=new ArrayList<String>();

    }
    public int getIcon(){return icon;}
    public void setIcon(int icon){this.icon=icon;}

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

    public void setFriendcord(String friendcord) {
        this.friendcord = friendcord;
    }

    public String getFriendcord() {
        return friendcord;
    }

}
