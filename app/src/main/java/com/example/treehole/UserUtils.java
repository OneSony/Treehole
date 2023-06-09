package com.example.treehole;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import me.pushy.sdk.Pushy;

public class UserUtils {
    public static SharedPreferences userDetails;
    public static SharedPreferences deviceDetails;
    private static final String USER_PREF = "USER_DETAILS";
    private static final String USERNAME = "USERNAME";

    private static final String USERID = "USERID";
    private static final String DEVICE_PREF = "DEVICE_DETAILS";
    private static final String DEVICETOKEN = "DEVICETOKEN";

    private Context context;

    public static void init(Context context) {
        context = context;
        userDetails = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        deviceDetails = context.getSharedPreferences(DEVICE_PREF, MODE_PRIVATE);
    }

    public static class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Object> {
        Activity mActivity;
        String ALREADY_REGISTERED = "[ALREADY REGISTERED DEVICE TOKEN]";

        public RegisterForPushNotificationsAsync(Activity activity) {
            this.mActivity = activity;
        }

        protected Object doInBackground(Void... params) {
            try {
                String deviceToken = "";
                if (!Pushy.isRegistered(mActivity)) {
                // Register the device for notifications (replace MainActivity with your Activity class name)
                    deviceToken = Pushy.register(mActivity);
                    SharedPreferences.Editor editor = deviceDetails.edit();
                    editor.putString(DEVICETOKEN, deviceToken);
                    editor.apply();
                } else{
                    return ALREADY_REGISTERED;
                }

                // Registration succeeded, log token to logcat
                Log.d("Pushy", "Pushy device token: " + deviceToken);


                // Provide token to onPostExecute()
                return deviceToken;
            }
            catch (Exception exc) {
                // Registration failed, provide exception to onPostExecute()
                return exc;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            String message;

            // Registration failed?
            if (result instanceof Exception) {
                // Log to console
                Log.e("Pushy", result.toString());

                // Display error in alert
                message = ((Exception) result).getMessage();
            }
            else {
                if (result.toString().equals(ALREADY_REGISTERED)){
                    message = "Pushy device token already registered";
                } else {
                    message = "Pushy device token: " + result.toString() + "\n\n(copy from logcat)";
                }

            }

            // DEBUG
            // Registration succeeded, display an alert with the device token
            new android.app.AlertDialog.Builder(this.mActivity)
                    .setTitle("Pushy")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    public static void clearUser(){
        SharedPreferences.Editor editor = userDetails.edit();
        editor.clear();
        editor.apply();
    }

    public static void isLoggedIn(WebUtils.WebCallback callback) {
        WebUtils.sendGet("/users/is_logged_in/", false, callback);
    }

    public static void login(String username, String password, WebUtils.WebCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebUtils.sendPost("/users/login/", false, json, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                JSONObject registerData = new JSONObject();
                try {
                    registerData.put("token", deviceDetails.getString(DEVICETOKEN, ""));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                WebUtils.sendPost("/messaging/device/", true, registerData, new WebUtils.WebCallback() {
                    @Override
                    public void onSuccess(JSONObject deviceResponseJson) {
                        callback.onSuccess(json);
                    }

                    @Override
                    public void onError(Throwable t) {
                        callback.onError(t);
                    }

                    @Override
                    public void onFailure(JSONObject deviceResponseJson) {
                        callback.onFailure(deviceResponseJson);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }

            @Override
            public void onFailure(JSONObject json) {
                callback.onFailure(json);
            }
        });
    }

    public static void logout(WebUtils.WebCallback callback) {
        WebUtils.sendGet("/users/logout/", false, callback);
    }

    public static void setUsername(String username){
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public static void setUserid(String userid){
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString(USERID, userid);
        editor.apply();
    }

    public static String getUsername(){
        return userDetails.getString(USERNAME, "");
    }

    public static String getUserid(){
        return userDetails.getString(USERID, "");
    }
}
