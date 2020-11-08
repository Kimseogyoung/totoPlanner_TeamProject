package com.example.teamproject_toto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberInfo {
    private String name;
    private String phoneNumber;
    private Map planner;

    public MemberInfo(String name, String phoneNumber){
        this.name=name;
        this.phoneNumber=phoneNumber;
        Map<String, ArrayList> planner = new HashMap<String, ArrayList>();
        planner.put("null", null);
        this.planner = planner;
    }

    public String getName(){return name;}
    public void setName(String n){name=n;}


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPlanner(HashMap<String, Array> planner){
        this.planner = planner;
    }

    public Map<String, ArrayList> getPlanner() {
        return planner;
    }
}
