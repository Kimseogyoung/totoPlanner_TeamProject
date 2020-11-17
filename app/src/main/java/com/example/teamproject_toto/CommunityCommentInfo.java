package com.example.teamproject_toto;

public class CommunityCommentInfo {
    String date;
    String comment;

    CommunityCommentInfo(String date, String comment){
        this.date = date;
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}