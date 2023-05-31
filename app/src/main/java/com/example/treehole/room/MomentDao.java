package com.example.treehole.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MomentDao {


    @Query("SELECT * FROM moment_table")
    LiveData<List<Moment>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Moment moment);

    @Query("DELETE FROM moment_table")
    void deleteAll();

    @Query("SELECT * FROM moment_table ORDER BY m_index ASC")
    DataSource.Factory<Integer, Moment> getItems();
}
