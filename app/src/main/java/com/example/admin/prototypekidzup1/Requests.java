package com.example.admin.prototypekidzup1;

/**
 * Created by ADMIN on 03.12.2017.
 */

public class Requests {

    String from, type;
    long time;

    public Requests() {

    }

    public Requests(String from, String type, long time) {
        this.from = from;
        this.type = type;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
