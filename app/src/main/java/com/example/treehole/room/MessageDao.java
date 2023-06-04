package com.example.treehole.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message message);

    @Query("SELECT * FROM message_table ORDER BY `index` ASC")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM message_table WHERE `index` = :index")
    LiveData<Message> getMessageByIndex(int index);

    @Query("SELECT * FROM message_table WHERE `index` = :index")
    Message _getMessageByIndex(int index);

    @Update
    void updateMessage(Message message);

    @Transaction
    default void addMessageNode(int index, MessageNode messageNode) {
        Message message = _getMessageByIndex(index);
        if (message != null) {
            message.getNodes().add(messageNode);
            updateMessage(message);
        }
    }

}
