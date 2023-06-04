package com.example.treehole.room;

public class MessageNode {
    private int index;
    private int user;
    private String text;

    public MessageNode(int user, String text){
        this.user=user;
        this.text=text;
    }

    public int getIndex(){
        return index;
    }

    public int getUser(){
        return user;
    }

    public String getText(){
        return text;
    }
}
