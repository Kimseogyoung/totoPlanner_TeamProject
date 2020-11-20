package com.example.teamproject_toto;

// CommunityboardInfo.java 작성자 : 이아연
// 커뮤니티의 게시글의 정보
public class CommunityboardInfo {
    String nickname; // 닉네임
    String title; // 제목
    String content; // 내용
    String date; // 날짜
    String img=""; // 이미지

    public CommunityboardInfo(String nickname, String title, String content, String date, String img){
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.date = date;
        this.img = img;
    }

    // set Method
    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    // get method
    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }
}
