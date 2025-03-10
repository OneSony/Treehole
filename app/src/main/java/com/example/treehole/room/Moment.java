package com.example.treehole.room;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

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

    private int comment_num;

    private boolean isLiked=false;

    private boolean isFavourite=false;

    private String text_type;

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


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+08:00'");
        try {
            this.date = inputFormat.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setNum(int likes_num,int comment_num,int favourite_num){
        this.likes_num=likes_num;
        this.comment_num=comment_num;
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

    public void setLiked(Boolean isLiked,Boolean isFavourite){
        this.isLiked=isLiked;
        this.isFavourite=isFavourite;
    }

    public void setText_type(String text_type){
        this.text_type=text_type;
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

            if (minutesDifference < 3) {
                return "现在";
            }

            if (minutesDifference < 60) {
                return minutesDifference + "分钟前";
            }

            long hoursDifference = TimeUnit.MILLISECONDS.toHours(millisecondsDifference);
            if (hoursDifference < 24) {
                return hoursDifference + "小时前";
            }


            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);

            Calendar givenCalendar = Calendar.getInstance();
            givenCalendar.setTime(date);

            if (currentCalendar.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM'月'dd'日'");
                return dateFormat.format(date);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
            return dateFormat.format(date);

        }else {
            return date_str;
        }
    }

    public String getFullDate(){
        if(date!=null) {
            Date currentDate = new Date(); // 当前时间的Date对象

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);

            Calendar givenCalendar = Calendar.getInstance();
            givenCalendar.setTime(date);

            if (currentCalendar.get(Calendar.YEAR) == givenCalendar.get(Calendar.YEAR)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM'月'dd'日' HH:mm");
                return dateFormat.format(date);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH:mm");
            return dateFormat.format(date);

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

    public int getComment_num() {
        return comment_num;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getText_type(){
        return text_type;
    }

    public Boolean isLiked(){
        return isLiked;
    }

    public Boolean isFavourite(){
        return isFavourite;
    }

    public void likes_num_add(){
        likes_num++;
    }

    public void likes_num_minus(){
        likes_num--;
    }

    public void favourite_num_add(){
        favourite_num++;
    }

    public void favourite_num_minus() {
        favourite_num--;
    }


}

