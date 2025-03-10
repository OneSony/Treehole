package com.example.treehole.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

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

    public void addMessageNode(int index, MessageNode messageNode) {//自己发送消息的接口
        new insertNodeAsyncTask(messageDao, index, messageNode).execute();
    }

    public int searchMessage(String user_id,String username) throws ExecutionException, InterruptedException {
        return new searchAsyncTask(messageDao,user_id,username).execute().get();
    }

    public void receiveMessageNode(String user_id, String username, MessageNode messageNode) {//收到消息的接口
        new newMessageNodeAsyncTask(messageDao, user_id,username, messageNode).execute();
    }

    public void receiveMessageNode(String user_id, String username, MessageNode messageNode,int index) {//收到消息的接口
        new newMessageNodeAsyncTaskFilter(messageDao, user_id,username, messageNode,index).execute();
    }

    public void cleanMessageUnread(int index){
        new cleanMessageUnreadAsyncTask(messageDao,index).execute();
    }

    public void deleteMessageByIndex(int index){
        new deleteIndexAsyncTask(messageDao,index).execute();
    }

    public void deleteAllMessage(){
        new deleteAllAsyncTask(messageDao).execute();
    }


    public void updateUserInfo(String user_id,String username){
        new updateUserInfoAsyncTask(messageDao,user_id,username).execute();
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

    private static class insertNodeAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private MessageNode messageNode;

        private int index;

        insertNodeAsyncTask(MessageDao dao,int index,MessageNode messageNode){
            mAsyncTaskDao=dao;
            this.messageNode=messageNode;
            this.index=index;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mAsyncTaskDao.addMessageNodeWithoutUnread(index,messageNode);
            return null;
        }
    }

    private static class searchAsyncTask extends AsyncTask<Void,Void,Integer> {
        private MessageDao mAsyncTaskDao;
        private String user_id;
        private String username;

        searchAsyncTask(MessageDao dao,String user_id,String username){
            mAsyncTaskDao=dao;
            this.user_id=user_id;
            this.username=username;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return mAsyncTaskDao.searchAndCreateMessage(user_id,username);
        }
    }


    private static class deleteIndexAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private int index;

        deleteIndexAsyncTask(MessageDao dao,int index){
            mAsyncTaskDao=dao;
            this.index=index;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteMessageByIndex(index);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;

        deleteAllAsyncTask(MessageDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllMessages();
            return null;
        }
    }

    private static class newMessageNodeAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private MessageNode messageNode;
        private String user_id;
        private String username;

        newMessageNodeAsyncTask(MessageDao dao,String user_id,String username,MessageNode messageNode){
            this.mAsyncTaskDao=dao;
            this.messageNode=messageNode;
            this.user_id=user_id;
            this.username=username;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int message_index=mAsyncTaskDao.searchAndCreateMessage(user_id,username);
            mAsyncTaskDao.addMessageNode(message_index,messageNode);
            return null;
        }
    }

    private static class newMessageNodeAsyncTaskFilter extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private MessageNode messageNode;
        private String user_id;
        private String username;

        private int filter_index;

        newMessageNodeAsyncTaskFilter(MessageDao dao,String user_id,String username,MessageNode messageNode,int filter_index){
            this.mAsyncTaskDao=dao;
            this.messageNode=messageNode;
            this.user_id=user_id;
            this.username=username;
            this.filter_index=filter_index;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int message_index=mAsyncTaskDao.searchAndCreateMessage(user_id,username);
            mAsyncTaskDao.addMessageNode(message_index,messageNode,filter_index);
            return null;
        }
    }

    private static class cleanMessageUnreadAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private int index;
        cleanMessageUnreadAsyncTask(MessageDao dao,int index){
            this.mAsyncTaskDao=dao;
            this.index=index;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.cleanMessageUnread(index);
            return null;
        }
    }


    private static class updateUserInfoAsyncTask extends AsyncTask<Void,Void,Void> {
        private MessageDao mAsyncTaskDao;
        private String user_id;
        private String username;
        updateUserInfoAsyncTask(MessageDao dao,String user_id,String username){
            this.mAsyncTaskDao=dao;
            this.user_id=user_id;
            this.username=username;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.updateUsernameByUserId(user_id,username);
            Log.d("update","update in repository");
            return null;
        }
    }

}
