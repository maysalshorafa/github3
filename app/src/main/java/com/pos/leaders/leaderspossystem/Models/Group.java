package com.pos.leaders.leaderspossystem.Models;

import android.content.Context;

/**
 * Created by Win8.1 on 6/21/2017.
 */

public class Group {

    private long id;
    private String name ;
    private String description;
    private int type;
    private float parcent;
    private int amount;
    private int point;

    public Group(long id, String name, String description, int type, float parcent, int amount, int point) {
        this.id=id;
        this.name=name;
        this.description=description;
        this.type=type;
        this.parcent=parcent;
        this.amount=amount;
        this.point=point;
    }

    public Group(Context context) {
    }

    public Group(String name) {
        this.name = name;
    }


    public void setname(String groupname) {
        this.name = groupname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setParcent(float parcent) {
        this.parcent = parcent;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPoint(int point) {
        this.point = point;
    }


    public String getname() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public float getParcent() {
        return parcent;
    }

    public int getAmount() {
        return amount;
    }

    public int getPoint() {
        return point;
    }

}
