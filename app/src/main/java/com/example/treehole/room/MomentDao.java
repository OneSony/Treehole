package com.example.treehole.room;

/*@Dao
public interface MomentDao {


    @Query("SELECT * FROM moment_table")
    LiveData<List<Moment>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Moment moment);

    @Query("DELETE FROM moment_table")
    void deleteAll();

    @Query("SELECT * FROM moment_table ORDER BY m_index ASC")
    DataSource.Factory<Integer, Moment> getItems();

    //List<Moment> getMomentsFromIndex(int startIndex);
    //DataSource.Factory<Integer, Moment> getMomentsFromIndex(int startIndex);
    @Query("SELECT * FROM moment_table WHERE m_index > :startIndex LIMIT 1")
    List<Moment> getMomentsFromIndex(int startIndex);

    @Query("SELECT COUNT(*) FROM moment_table")
    int getMomentCount();

    @Query("SELECT * FROM moment_table WHERE m_index > :currentIndex ORDER BY m_index ASC LIMIT 2")
    List<Moment> getNextMoments(int currentIndex);

}
*/