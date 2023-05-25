package com.example.treehole.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Moment {
    @PrimaryKey
    public int m_index;

    @ColumnInfo(name="topic")
    public String topic;

    @ColumnInfo(name="text")
    public String text;

}
