package com.example.admin.prototypekidzup1;

/**
 * Created by ADMIN on 05.12.2017.
 */

public class Tasks {

    String from,
    icon_path,
    prize_icon_path,
    prize_text,
    punish_icon_path,
    punish_text,
    seen,
    text,
    title,
    to;

    public Tasks() {

    }

    public Tasks(String from, String icon_path, String prize_icon_path, String prize_text, String punish_icon_path, String punish_text, String seen, String text, String title, String to) {
        this.from = from;
        this.icon_path = icon_path;
        this.prize_icon_path = prize_icon_path;
        this.prize_text = prize_text;
        this.punish_icon_path = punish_icon_path;
        this.punish_text = punish_text;
        this.seen = seen;
        this.text = text;
        this.title = title;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }

    public String getPrize_icon_path() {
        return prize_icon_path;
    }

    public void setPrize_icon_path(String prize_icon_path) {
        this.prize_icon_path = prize_icon_path;
    }

    public String getPrize_text() {
        return prize_text;
    }

    public void setPrize_text(String prize_text) {
        this.prize_text = prize_text;
    }

    public String getPunish_icon_path() {
        return punish_icon_path;
    }

    public void setPunish_icon_path(String punish_icon_path) {
        this.punish_icon_path = punish_icon_path;
    }

    public String getPunish_text() {
        return punish_text;
    }

    public void setPunish_text(String punish_text) {
        this.punish_text = punish_text;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
