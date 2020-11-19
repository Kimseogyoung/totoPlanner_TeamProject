package com.example.teamproject_toto;

//타임라인 게시글 정보 클래스
//파이어베이스에 저장될 문서의 데이터필드를 set하고, 타임라인에 글을 출력할 때 사용
public class TimelineboardInfo {
    String writercode;//작성자의 회원uid
    String name; //작성자 이름
    String date; //작성 날짜
    String title=""; //글 제목
    String img="";//첨부한 사진의 storge 이름
    String content=""; //내용
    String id="";//문서 id

    public TimelineboardInfo(String writercode ,String name, String date, String title, String img, String content,String id){
        this.writercode=writercode;
        this.name=name;
        this.date=date;
        this.title=title;
        this.img=img;
        this.content=content;
        this.id=id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getWritercode() {
        return writercode;
    }

    public void setWritercode(String writercode) {
        this.writercode = writercode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
