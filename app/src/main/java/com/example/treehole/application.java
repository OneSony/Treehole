package com.example.treehole;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.treehole.room.MessageNode;
import com.example.treehole.room.MessageQueueNode;

import java.util.ArrayList;

public class application extends Application {

    private MutableLiveData<ArrayList<MessageQueueNode>> messageNodeQueueLiveData;

    @Override
    public void onCreate() {
        super.onCreate();
        messageNodeQueueLiveData = new MutableLiveData<>(new ArrayList<>());

        IntentFilter filter = new IntentFilter("com.example.treehole.NEW_MESSAGE_RECEIVED");
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 处理收到的广播消息
            String senderId = intent.getStringExtra("senderId");
            String senderUsername = intent.getStringExtra("senderUsername");
            String message = intent.getStringExtra("message");

            ArrayList<MessageQueueNode> currentQueue = messageNodeQueueLiveData.getValue();
            if (currentQueue == null) {
                currentQueue = new ArrayList<>();
            }

            currentQueue.add(new MessageQueueNode(senderId, senderUsername, new MessageNode(0, message)));

            messageNodeQueueLiveData.setValue(currentQueue);
        }
    };

    public void clearMessageQueue() {
        messageNodeQueueLiveData.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<MessageQueueNode>> getUnreadMessagesLiveData() {
        return messageNodeQueueLiveData;
    }
}
