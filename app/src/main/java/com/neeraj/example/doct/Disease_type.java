package com.neeraj.example.doct;

/**
 * Created by neera on 3/24/2017.
 */

public class Disease_type {
    private String DISEASE,PROB,ID;
    public Disease_type(){
        ID="";
        DISEASE="";
        PROB="";
    }
    public Disease_type(String id,String disease,String prob)
    {
        this.ID=id;
        this.DISEASE=disease;
        this.PROB=prob;
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public String getDISEASE() {
        return DISEASE;
    }

    public void setDISEASE(String DISEASE) {
        this.DISEASE = DISEASE;
    }

    public String getPROB() {
        return PROB;
    }

    public void setPROB(String PROB) {
        this.PROB = PROB;
    }
}
