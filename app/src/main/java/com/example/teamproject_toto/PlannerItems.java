package com.example.teamproject_toto;

// PlannerItems.java 작성자 : 이아연
// Planner의 일정 아이템
public class PlannerItems  {

    private String text ; // 일정
    private boolean cv; // 체크 여부
    private boolean uploaded; // 업로드 여부

    public PlannerItems(String text, boolean cv ,boolean uploaded){
        this.text = text;
        this.cv = cv;
        this.uploaded=uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
    public Boolean getUploaded(){
        return uploaded;
    }

    public void setText(String text) {
        this.text = text ;
    }
    public String getText() {
        return this.text ;
    }

    public boolean getCv() {
        return cv;
    }

    public void setCv(boolean bool){
        this.cv = bool;
    }
}
