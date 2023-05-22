package com.example.treehole;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class dot implements Serializable {
    private int index=0;
    private String topic="";
    private String text="";
    private String auth="";
    private Date date;
    //private String photo;
    private int comment_num;
    private int like_num;
    private int collect_num;

    private int[] photo_index=new int[3];

    private int photo_num;

    private int profile_index;

    private boolean path_flag;

    private String photo_path;

    private void init(int _index,String _topic,String _text,String _auth,int _profile_index){
        this.index=_index;
        this.topic=_topic;
        this.text=_text;
        this.auth=_auth;

        this.comment_num=0;
        this.like_num=0;
        this.collect_num=0;

        this.date=new java.sql.Date(System.currentTimeMillis());

        profile_index=_profile_index;
        path_flag=false;
    }
    /*public dot(int _index,String _topic,String _text,String _auth,int _profile_index,int _photo_num,int index1,int index2,int index3){
        this.index=_index;
        this.topic=_topic;
        this.text=_text;
        this.auth=_auth;

        this.comment_num=0;
        this.like_num=0;
        this.collect_num=0;

        this.date=new java.sql.Date(System.currentTimeMillis());

        profile_index=_profile_index;

        photo_num=_photo_num;


        Log.d("TEST",String.valueOf(index1));
        Log.d("TEST",String.valueOf(index2));
        Log.d("TEST",String.valueOf(index3));

        if(photo_num==1){
            photo_index[0]=index1;
        }else if(photo_num==2){
            photo_index[0]=index1;
            photo_index[1]=index2;
        }else if(photo_num==3){
            photo_index[0]=index1;
            photo_index[1]=index2;
            photo_index[2]=index3;
        }

    }*/

    public dot(int _index,String _topic,String _text,String _auth,int _profile_index,int index1,int index2,int index3){
        init(_index,_topic,_text,_auth,_profile_index);
        photo_num=3;
        photo_index[0]=index1;
        photo_index[1]=index2;
        photo_index[2]=index3;
    }

    public dot(int _index,String _topic,String _text,String _auth,int _profile_index,int index1,int index2){
        init(_index,_topic,_text,_auth,_profile_index);
        photo_num=2;
        photo_index[0]=index1;
        photo_index[1]=index2;
    }

    public dot(int _index,String _topic,String _text,String _auth,int _profile_index,int index1){
        init(_index,_topic,_text,_auth,_profile_index);
        photo_num=1;
        photo_index[0]=index1;
    }

    public dot(int _index,String _topic,String _text,String _auth,int _profile_index){
        init(_index,_topic,_text,_auth,_profile_index);
        photo_num=0;
    }

    public dot(int _index,String _topic,String _text,String _auth,int _profile_index,String _photo_path){
        init(_index,_topic,_text,_auth,_profile_index);
        path_flag=true;
        photo_path=_photo_path;
        photo_num=0;
    }


    public int getIndex(){
        return index;
    }

    public String getTopic(){
        return topic;
    }

    public String getText(){
        return text;
    }

    public String getAuth(){
        return auth;
    }

    public String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return format.format(date);
    }

    public int getLike_num(){
        return like_num;
    }

    public int getComment_num(){
        return comment_num;
    }

    public int getCollect_num(){
        return collect_num;
    }

    public int getPhoto_num(){
        return photo_num;
    }

    public int getPhoto_index(int i){
        //Log.d("GetPhoto",String.valueOf([i]));
        return photo_index[i];
    }

    public int getProfile_index(){
        return profile_index;
    }

    public void setLike_num(int _num){
        like_num=_num;
    }

    public boolean isPath_flag(){
        return path_flag;
    }

    public String getPhoto_path(){return photo_path;}
}
