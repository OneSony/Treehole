package com.example.treehole.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.treehole.UserUtils;

@Database(entities = {Message.class},version = 1,exportSchema = false)
@TypeConverters(MessageNodeConverter.class)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
    private static MessageDatabase INSTANCE;

    public static MessageDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (MessageDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),MessageDatabase.class,"message_database_"+ UserUtils.getUserid()).addCallback(sOnOpenCallback).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sOnOpenCallback=new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            initializeData();
        }
    };

    private static void initializeData() {

    }

    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
