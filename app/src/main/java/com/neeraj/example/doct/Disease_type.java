package com.neeraj.example.doct;

/**
 * Created by neera on 3/24/2017.
 */

public class Disease_type {
    private String DISEASE,PROB;
    public Disease_type(){
        DISEASE="";
        PROB="";
    }
    public Disease_type(String disease,String prob)
    {
        this.DISEASE=disease;
        this.PROB=prob;
    }

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
