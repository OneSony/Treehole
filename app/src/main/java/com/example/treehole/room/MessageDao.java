package com.example.treehole.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
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
    default void addMessageNode(int index, MessageNode messageNode) {//吧message的index传进来
        Message message = _getMessageByIndex(index);
        if (message != null) {
            message.getNodes().add(messageNode);
            message.plusUnread();
            updateMessage(message);
        }
    }

    @Transaction
    default void addMessageNodeWithoutUnread(int index, MessageNode messageNode) {//吧message的index传进来
        Message message = _getMessageByIndex(index);
        if (message != null) {
            message.getNodes().add(messageNode);
            updateMessage(message);
        }
    }

    @Query("SELECT * FROM message_table WHERE user_id = :userId")
    Message getMessageByUserId(String userId);

    @Transaction
    default int searchAndCreateMessage(String user_id,String username) {
        Message existingMessage = getMessageByUserId(user_id);
        if (existingMessage == null) {
            // 用户不存在，创建新的 Message
            Message newMessage = new Message(user_id,username, new ArrayList<>());
            insert(newMessage);
            return getMessageByUserId(user_id).getIndex();
        } else {
            // 用户已存在，返回匹配的消息
            return existingMessage.getIndex();
        }
    }

    @Transaction
    default void cleanMessageUnread(int index) {
        Message existingMessage = _getMessageByIndex(index);
        if (existingMessage == null) {

        } else {
            existingMessage.cleanUnread();
            updateMessage(existingMessage);
        }
    }

    @Query("DELETE FROM message_table WHERE `index` = :index")
    void deleteMessageByIndex(int index);

    @Query("DELETE FROM message_table")
    void deleteAllMessages();


}
