package com.example.treehole.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "moment_table")
public class Moment {
    @PrimaryKey(autoGenerate = true)
    public int m_index;

    @ColumnInfo(name="topic")
    public String topic;

    @ColumnInfo(name="text")
    public String text;

    public Moment(String topic,String text){
        this.topic=topic;
        this.text=text;
    }
}
