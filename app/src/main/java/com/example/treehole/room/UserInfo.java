package com.example.treehole.room;

public class UserInfo {
    private String user_id;
    private String username;

    public UserInfo() {
    }

    public UserInfo(String userId, String username) {
        this.user_id = userId;
        this.username = username;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

