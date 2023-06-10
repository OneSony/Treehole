package com.example.treehole;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import me.pushy.sdk.Pushy;
//import android.support.v4.app.NotificationCompat;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Parse values from Pushy
        String id = intent.getStringExtra("id");
        String sender_username = intent.getStringExtra("sender");
        String sender_id = String.valueOf(intent.getIntExtra("sender_id", 1));
        String type = intent.getStringExtra("type"); // string|image|video
        String message = intent.getStringExtra("message"); // String, or image/video url

        // Attempt to extract the "title" property from the data payload, or fallback to app shortcut label
        //String notificationTitle = intent.getStringExtra("title") != null ? intent.getStringExtra("info") : context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();

        if (id == null){
            id = "";
        }
        if (sender_username==null){
            sender_username = "";
        }
        if (type==null){
            type="";
        }
        if (message==null){
            message = "";
        }

        String notificationTitle = sender_username;

        // Attempt to extract the "message" property from the data payload: {"message":"Hello World!"}
        //String notificationText = intent.getStringExtra("message") != null ? intent.getStringExtra("message") : "Test notification";
        String notificationText = type;
        if (type.equals("string")) {
            notificationText = sender_username + ": " + message;
        }
        else if (type.equals("image")) {
            notificationText = sender_username + ": [图片]";
        }
        else if (type.equals("video")) {
            notificationText = sender_username+": [影片]";
        }

        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(null);
        //.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class)
        // Automatically configure a Notification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, context);

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        //
        // Use a random notification ID so multiple
        // notifications don't overwrite each other
        notificationManager.notify((int)(Math.random() * 100000), builder.build());

        //MsgActivity activity = (MsgActivity) context;
        //ChatViewModel viewModel = new ViewModelProvider((MsgActivity)context).get(ChatViewModel.class);
        //viewModel.receiveMessageNode(sender_id , sender_username, new MessageNode(0, message));

        //ChatViewModel viewModel = new ViewModelProvider().get(ChatViewModel.class);

        Intent broadcastIntent = new Intent("com.example.treehole.NEW_MESSAGE_RECEIVED");
        broadcastIntent.putExtra("senderId", sender_id);
        broadcastIntent.putExtra("senderUsername", sender_username);
        broadcastIntent.putExtra("message", message);

        // 发送广播
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

        // Write to database
        Log.d("MESSAGE RECEIVED", String.valueOf(id)+" "+type+""+message);
    }
}