package com.example.treehole.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "moment_table")
public class Moment_db implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int m_index;

    @ColumnInfo(name="topic")
    public String topic;

    @ColumnInfo(name="text")
    public String text;

    @ColumnInfo(name="id")
    public String id;

    public Moment_db(String topic,String text){
        this.topic=topic;
        this.text=text;
    }

    @Ignore
    public Moment_db(String id,String topic,String text){
        this.id=id;
        this.topic=topic;
        this.text=text;
    }

}
