package com.example.admin.prototypekidzup1;

/**
 * Created by ADMIN on 01.12.2017.
 */

public class Users {

    public String name;
    public String status;
    public String image;
    public String thumb_image;
    public String mode;
    public Long online;
    public String id;
    public String device_token;

    public Users() {
    }

    public Users(String name, String status, String image, String thumb_image, String mode, Long online, String id, String device_token) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.mode = mode;
        this.online = online;
        this.id = id;
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
