package com.example.teamproject_toto;

public class PlannerItems  {

    private String text ;
    private boolean cv;
    private boolean uploaded;

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