package com.example.treehole.room;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Comment {

    private String user_id;
    private String text;
    private String username;

    private String date_str;

    private Date date;

    public Comment(String user_id,String username, String text, String date_str) {
        this.user_id = user_id;
        this.username=username;
        this.text = text;
        this.date_str=date_str;

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        try {
            this.date = inputFormat.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUser_id() {
        return user_id;
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

    public String getText() {
        return text;
    }

    public String getUsername(){
        return username;
    }
}
