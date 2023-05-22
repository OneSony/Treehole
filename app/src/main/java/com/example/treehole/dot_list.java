package com.example.treehole;

import java.util.ArrayList;

public class dot_list {
    private ArrayList<dot> data=new ArrayList<>();
    private int count=0;

    public dot_list(){
    }

    /*public void insert(String _topic, String _text, String _auth,int profile_index, int _photo_num, int index1, int index2, int index3){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index,_photo_num,index1,index2,index3));
        count++;
    }*/

    public void insert(String _topic, String _text, String _auth,int profile_index, int index1, int index2, int index3){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index,index1,index2,index3));
        count++;
    }

    public void insert(String _topic, String _text, String _auth,int profile_index, int index1, int index2){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index,index1,index2));
        count++;
    }

    public void insert(String _topic, String _text, String _auth,int profile_index, int index1){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index,index1));
        count++;
    }

    public void insert(String _topic, String _text, String _auth,int profile_index){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index));
        count++;
    }

    public void insert(String _topic, String _text, String _auth,int profile_index,String _photo_path){
        data.add(0,new dot(count,_topic,_text,_auth,profile_index,_photo_path));
        count++;
    }

    public void delete(int index){
        data.remove(index);
        count--;
    }

    public dot get(int index){
        return data.get(index);
    }

    public int size(){
        return data.size();
    }
}
