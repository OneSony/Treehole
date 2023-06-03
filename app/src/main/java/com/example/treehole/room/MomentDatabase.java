package com.example.treehole.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Moment.class},version = 1,exportSchema = false)
public abstract class MomentDatabase extends RoomDatabase {
    public abstract MomentDao momentDao();
    private static MomentDatabase INSTANCE;


    public static MomentDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (MomentDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),MomentDatabase.class,"moment_database").addCallback(sOnOpenCallback).fallbackToDestructiveMigration().build();
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
}
