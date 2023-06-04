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

    @ColumnInfo(name="user")
    private String user;

    @ColumnInfo(name="nodes")
    private List<MessageNode> nodes;

    public Message(String user, List<MessageNode> nodes){
        this.user=user;
        this.nodes=nodes;
    }


    public int getIndex(){
        return index;
    }

    public List<MessageNode> getNodes(){
        return nodes;
    }

    public String getUser(){
        return user;
    }

    public void addNodes(MessageNode node){
        nodes.add(node);
    }
}