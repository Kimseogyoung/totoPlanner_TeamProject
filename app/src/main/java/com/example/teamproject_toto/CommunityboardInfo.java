package com.example.teamproject_toto;

import android.media.Image;

public class CommunityboardInfo {
    String nickname;
    String title;
    String content;
    String date;

    public CommunityboardInfo(String nickname, String title, String content, String date){
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.date = date;
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
}

