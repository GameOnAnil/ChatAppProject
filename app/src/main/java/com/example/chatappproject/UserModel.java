package com.example.chatappproject;

public class UserModel {
    public String username;
    public String image;
    public String status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserModel(){}
    public UserModel(String username, String image, String status) {
       this.username = username;
        this.image = image;
        this.status = status;


    }
}
