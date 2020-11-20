package com.example.teamproject_toto;

// CommunityCommentInfo.java 작성자 : 이아연
// 커뮤니티 게시글의 댓글 정보
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
