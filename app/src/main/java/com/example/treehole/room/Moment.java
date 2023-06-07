package com.example.treehole.room;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Moment implements Serializable {

    private String id;
    private String username;
    private String user_id;
    private String topic;
    private String text;

    private List<String> images;
    private List<String> videos;

    private List<String> tags;

    private String date_str;

    private Date date;

    private String location;
    private int likes_num;
    private int favourite_num;

    public Moment(String topic, String text){
        this.topic=topic;
        this.text=text;
    }

    public Moment(String id,String topic,String text){
        this.id=id;
        this.topic=topic;
        this.text=text;
    }

    public Moment(String id,String user_id,String username,String topic,String text,List<String> images,List<String> videos,List<String> tags,String date,String location,int likes_num,int favourite_num){
        this.id=id;
        this.topic=topic;
        this.text=text;
    }

    public Moment(String id, String user_id, String username, String topic, String text, String date){
        this.id=id;
        this.user_id=user_id;
        this.username=username;
        this.topic=topic;
        this.text=text;
        this.date_str=date;

        this.images=new ArrayList<>();
        this.videos=new ArrayList<>();
        this.tags=new ArrayList<>();
        this.location="";
        this.likes_num=0;
        this.favourite_num=0;


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        try {
            this.date = inputFormat.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setNum(int likes_num,int favourite_num){
        this.likes_num=likes_num;
        this.favourite_num=favourite_num;
    }

    public void setTags(List<String> tags){
        this.tags=tags;
    }

    public void setLocation(String location){
        this.location=location;
    }

    public void setMedias(List<String> images,List<String> videos){
        if(images.size()!=0){
            this.images=images;
        }else{
            if(videos.size()!=0){
                this.videos=videos;
            }else{//都没有
            }
        }
    }



    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTopic() {
        return topic;
    }

    public String getText() {
        return text;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public String getDate() {

        if(date!=null) {
            Date currentDate = new Date(); // 当前时间的Date对象

            long millisecondsDifference = currentDate.getTime() - date.getTime();

            long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(millisecondsDifference);
            if (minutesDifference < 60) {
                return minutesDifference + "分钟前";
            }

            long hoursDifference = TimeUnit.MILLISECONDS.toHours(millisecondsDifference);
            if (hoursDifference < 24) {
                return hoursDifference + "小时前";
            }

            long daysDifference = TimeUnit.MILLISECONDS.toDays(millisecondsDifference);
            return daysDifference + "天前";
        }else {
            return date_str;
        }
    }

    public int getLikes_num() {
        return likes_num;
    }

    public int getFavourite_num() {
        return favourite_num;
    }
}

