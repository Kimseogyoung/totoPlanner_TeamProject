package com.example.teamproject_toto;

import java.util.ArrayList;

//사용자의 회원정보 클래스
//데이터베이스의 users컬렉션 문서들의 데이터필드는 이 클래스로 저장됨
public class MemberInfo {
    private String icon;//프로필사진 이름
    private String name;//이름
    private String id;//아이디
    private String phoneNumber;//전화번호
    private ArrayList<String> friends;//친구목록
    private String friendcode;//친구코드

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
