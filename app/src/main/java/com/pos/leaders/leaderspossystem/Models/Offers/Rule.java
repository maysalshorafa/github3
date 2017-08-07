package com.pos.leaders.leaderspossystem.Models.Offers;

/**
 * Created by KARAM on 31/07/2017.
 */

public abstract class Rule {

    public static final String RULE1 = "RULE1";
    public static final String RULE2 = "RULE2";
    public static final String RULE3 = "RULE3";
    public static final String RULE4 = "RULE4";
    public static final String RULE5 = "RULE5";
    public static final String RULE6 = "RULE6";
    public static final String RULE7 = "RULE7";
    public static final String RULE8 = "RULE8";
    public static final String RULE9 = "RULE9";
    public static final String RULE10 = "RULE10";
    public static final String RULE11 = "RULE11";
    public static final String RULE12 = "RULE12";

    public static final String OPEN = "open";
    public static final String CLOSE = "close";
    public static final String BENDING = "bending";
    public static final String STOP = "stop";

    int id;
    String type;

    public Rule(int id,String type){
        this.id = id;
        this.type = type;
    }

    public Rule(String type){
        this.type = type;
    }
}
