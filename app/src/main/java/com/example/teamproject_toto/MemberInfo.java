package com.example.teamproject_toto;

public class MemberInfo {
    private String name;
    private String phoneNumber;

    public MemberInfo(String name, String phoneNumber){
        this.name=name;
        this.phoneNumber=phoneNumber;
    }

    public String getName(){return name;}
    public void setName(String n){name=n;}


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
