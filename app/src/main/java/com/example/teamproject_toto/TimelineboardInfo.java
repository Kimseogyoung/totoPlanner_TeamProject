package com.example.teamproject_toto;

public class TimelineboardInfo {
    int icon=0;
    String name;
    String date;
    String title="";
    String img="";
    String content="";

    public TimelineboardInfo( String name, String date, String title, String img, String content){
        this.name=name;
        this.date=date;
        this.title=title;
        this.img=img;
        this.content=content;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
