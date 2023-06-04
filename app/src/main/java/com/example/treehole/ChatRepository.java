package com.example.treehole;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.treehole.room.Message;
import com.example.treehole.room.MessageDao;
import com.example.treehole.room.MessageDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> allMessage;

    public ChatRepository(Application application){
        MessageDatabase db=MessageDatabase.getDatabase(application);
        messageDao=db.messageDao();
        allMessage=messageDao.getAllMessages();
    }

    public LiveData<List<Message>> getAllMessage(){
        return allMessage;
    }

    public void insert(Message message){
        new insertAsyncTask(messageDao).execute(message);
    }

    public LiveData<Message> getMessageByIndex(int index) throws ExecutionException, InterruptedException {
        return new getAsyncTask(messageDao,index).execute().get();
    }

    private static class insertAsyncTask extends AsyncTask<Message,Void,Void> {
        private MessageDao mAsyncTaskDao;

        insertAsyncTask(MessageDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected Void doInBackground(Message... messages) {
            mAsyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    private static class getAsyncTask extends AsyncTask<Integer,Void,LiveData<Message>> {
        private MessageDao mAsyncTaskDao;
        private int index;

        getAsyncTask(MessageDao dao,int index){
            mAsyncTaskDao=dao;
            this.index=index;
        }

        @Override
        protected LiveData<Message> doInBackground(Integer... integers) {
            return mAsyncTaskDao.getMessageByIndex(index);
        }
    }

}
