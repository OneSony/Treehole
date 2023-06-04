package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.treehole.room.Message;

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
}
