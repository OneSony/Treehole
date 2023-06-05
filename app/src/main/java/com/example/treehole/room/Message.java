package com.example.treehole.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "message_table")
public class Message implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int index;

    @ColumnInfo(name="user_id")
    private String user_id;

    @ColumnInfo(name="username")
    private String username;

    @ColumnInfo(name="nodes")
    private List<MessageNode> nodes;

    public Message(String user_id,String username, List<MessageNode> nodes){
        this.user_id=user_id;
        this.username=username;
        this.nodes=nodes;
    }


    public int getIndex(){
        return index;
    }

    public List<MessageNode> getNodes(){
        return nodes;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getUsername(){
        return username;
    }

    public void addNodes(MessageNode node){
        nodes.add(node);
    }
}