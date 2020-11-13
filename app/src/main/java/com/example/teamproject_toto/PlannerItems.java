package com.example.teamproject_toto;


import android.widget.CheckBox;

public class PlannerItems  {

    private String text ;
    private boolean cv;

    public PlannerItems(String text, boolean cv){
        this.text = text;
        this.cv = cv;
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