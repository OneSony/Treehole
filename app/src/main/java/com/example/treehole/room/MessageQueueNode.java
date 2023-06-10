package com.example.treehole.room;

public class MessageQueueNode {

    private String senderId;
    private String senderUsername;
    private MessageNode messageNode;

    public MessageQueueNode(String senderId,String senderUsername,MessageNode messageNode){
        this.senderId=senderId;
        this.senderUsername=senderUsername;
        this.messageNode=messageNode;
    }

    public String getSenderId(){
        return senderId;
    }

    public String getSenderUsername(){
        return senderUsername;
    }

    public MessageNode getMessageNode(){
        return messageNode;
    }
}
