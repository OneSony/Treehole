package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository mRepository;
    private LiveData<List<Message>> mAllMessage;


    public ChatViewModel(@NonNull Application application) {
        super(application);
        mRepository=new ChatRepository(application);
        mAllMessage=mRepository.getAllMessage();
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

    public void deleteMessageByIndex(int index){
        mRepository.deleteMessageByIndex(index);
    }

    public void deleteAllMessage(){
        mRepository.deleteAllMessage();
    }
}
