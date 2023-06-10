package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;
import com.example.treehole.room.MessageQueueNode;
import com.example.treehole.room.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository mRepository;
    private LiveData<List<Message>> mAllMessage;

    private LiveData<ArrayList<MessageQueueNode>> messageNodeQueueLiveData;


    public ChatViewModel(@NonNull Application application) {
        super(application);
        mRepository=new ChatRepository(application);
        mAllMessage=mRepository.getAllMessage();
/*
        application myApplication = (application) getApplication();


        Observer<ArrayList<MessageQueueNode>> unreadMessagesObserver = messageQueue -> {
            // 更新未读消息列表

            ArrayList<MessageQueueNode> unreadMessages = new ArrayList<>();
            unreadMessages.addAll(messageQueue);
            //unreadMessages.clear();

            Log.d("GOT IN chat viewmodel",String.valueOf(messageQueue.size()));

            // 处理未读消息
            for (MessageQueueNode messageQueueNode : unreadMessages) {
                receiveMessageNode(messageQueueNode.getSenderId(), messageQueueNode.getSenderUsername(), messageQueueNode.getMessageNode());
            }

            messageQueue.clear();
        };

        messageNodeQueueLiveData=myApplication.getUnreadMessagesLiveData();

        messageNodeQueueLiveData.observe(this.getApplication(), unreadMessagesObserver);


 */
    }

    public LiveData<List<Message>> getAllMessage(){
        return mAllMessage;
    }

    public void insert(Message message){
        mRepository.insert(message);
    }

    public LiveData<Message> getMessageByIndex(int index) throws ExecutionException, InterruptedException {
        return mRepository.getMessageByIndex(index);
    }

    public void addMessageNode(int index, MessageNode messageNode) {
        mRepository.addMessageNode(index, messageNode);
    }

    public int searchMessage(String user_id,String username) throws ExecutionException, InterruptedException {//按照user_id搜索已有聊天记录，如果有返回对应Message，如果没有则创建并返回Message
        return mRepository.searchMessage(user_id,username);
    }

    public void receiveMessageNode(String user_id, String username, MessageNode messageNode) {//收到消息的接口
        mRepository.receiveMessageNode(user_id,username, messageNode);
    }

    public void cleanMessageUnread(int index){
        mRepository.cleanMessageUnread(index);
    }

    public void deleteMessageByIndex(int index){
        mRepository.deleteMessageByIndex(index);
    }

    public void deleteAllMessage(){
        mRepository.deleteAllMessage();
    }

    public LiveData<List<UserInfo>> getAllUserInfo() throws ExecutionException, InterruptedException {
        return mRepository.getAllUserInfo();
    }

    public void updateUserInfo(String user_id,String username){
        mRepository.updateUserInfo(user_id,username);
    }
}
