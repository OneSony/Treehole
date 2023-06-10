package com.example.treehole.room;

public class Comment {

    private String user_id;
    private String text;
    private String date;
    private String username;

    public Comment(String user_id,String username, String text, String date) {
        this.user_id = user_id;
        this.username=username;
        this.text = text;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getUsername(){
        return username;
    }
}
