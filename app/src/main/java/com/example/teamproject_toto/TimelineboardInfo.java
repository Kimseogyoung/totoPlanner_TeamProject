package com.example.teamproject_toto;

public class TimelineboardInfo {
    String writercode;
    String name;
    String date;
    String title="";
    String img="";
    String content="";
    String id="";

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
