package com.example.treehole.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MomentDao {


    @Query("SELECT * FROM moment")
    LiveData<List<Moment>> getAll();

    @Insert
    void insert(Moment moment);

    @Delete
    void delete(Moment moment);
}
