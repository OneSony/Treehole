package com.example.treehole.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "message_table")
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int index;

    @ColumnInfo(name="nodes")
    private List<MessageNode> nodes;

    public int getIndex(){
        return index;
    }

    public List<MessageNode> getNodes(){
        return nodes;
    }

    public void addNodes(MessageNode node){
        nodes.add(node);
    }
}