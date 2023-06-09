package com.example.treehole;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebUtils {
    private static OkHttpClient client = null;
    public static SharedPreferences sessionDetails = null;
    private static String baseApiUrl;
    private static final String SESSION_ID = "SESSION_ID";
    private static final String SESSION_EXPIRY_DATE = "SESSION_EXPIRY_DATE";

    public interface WebCallback{
        void onSuccess(JSONObject json);
        void onError(Throwable t);
        void onFailure(JSONObject json);
    }

    private class MyHandler extends Handler {

        // Define a constant to identify the message type
        public static final int SET_TITLE = 1;
        public static final int SET_CONTENT = 2;
        public static final int SET_COUNT = 3;
        public static final int SET_IMAGE_PATH = 4;
        public static final int SET_VIDEO_PATH = 5;

        private String title = "";
        private String textContent = "";
        private int numOfFiles = 0;
        private List<String> imagePaths = new ArrayList<>();
        private List<String> videoPaths = new ArrayList<>();

        private boolean checkNumReached(){
            if (numOfFiles==0){
                throw new RuntimeException("Did not set number of files yet");
            }
            Log.d("HANDLER", "Submitted file progress: " + String.valueOf(imagePaths.size()+ videoPaths.size())+"/"+String.valueOf(numOfFiles));
            if ( (imagePaths.size()+ videoPaths.size()) >= numOfFiles ){
                return true;
            }return false;
        }

        private void sendPost() {
            if (!checkNumReached()){
                return;
            }

            //File imageFile = new File(Uri.parse(imagePaths.get(0)).getEncodedPath());
            //Log.d("HANDLER", "image: "+imageFile.getAbsolutePath());
            //File videoFile = new File(videoPaths.get(0));
            //Log.d("HANDLER", "video: "+videoPaths.get(0));
            File imageFile = new File(imagePaths.get(0));
            File videoFile = new File(videoPaths.get(0));
            Log.d("HANDLER", "image: "+imageFile.getAbsolutePath());
            Log.d("HANDLER", "video: "+videoFile.getAbsolutePath());
            RequestBody textBody = RequestBody.create(textContent, MediaType.parse("text/plain"));
            RequestBody imageBody = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
            RequestBody videoBody = RequestBody.create(videoFile, MediaType.parse("video/mp4"));
            // Build the multipart request body
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("text", null, textBody)
                    .addFormDataPart("image", imageFile.getName(), imageBody)
                    .addFormDataPart("video", videoFile.getName(), videoBody);


                /*
                // Build the request object
                Request request = new Request.Builder()
                        .url("https://rickyvu.pythonanywhere.com/posts/test/")
                        .post(requestBodyBuilder.build())
                        .build();

                // Send the request and handle the response
                Response response = null;
                // Send the request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle the error
                        Log.e("ERROR", e.getMessage());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle the response
                        String responseBody = response.body().string();
                        Log.d("RESPONSE", responseBody);
                    }
                });

                */
            WebUtils.sendPost("/posts/test/", true, requestBodyBuilder, new WebUtils.WebCallback() {
                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        Log.d("SUCCESS", json.getString("message"));
                        videoFile.delete();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    Log.e("ERROR", t.getMessage());
                    videoFile.delete();
                }

                @Override
                public void onFailure(JSONObject json) {
                    try {
                        Log.e("FAILURE", json.getString("message"));
                        videoFile.delete();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }


        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (msg.what == SET_TITLE){
                title = bundle.getString("title");
            }
            else if (msg.what == SET_CONTENT){
                textContent = bundle.getString("content");
            }
            else if (msg.what == SET_COUNT){
                numOfFiles = bundle.getInt("count");
            }
            else if(msg.what == SET_IMAGE_PATH) {
                String path = bundle.getString("path");
                imagePaths.add(path);
                sendPost();
            }
            else if(msg.what == SET_VIDEO_PATH) {
                String path = bundle.getString("path");
                videoPaths.add(path);
                sendPost();
            }

        }
    }
    public static UserUtils userUtils;

    public static void setBaseUrl(String apiUrl){
        baseApiUrl = apiUrl;
    }
    public static void setSessionDetails(Context context) {
        sessionDetails = context.getSharedPreferences("session_details", MODE_PRIVATE);
    }
    public static void setUser(Context context){
        userUtils = UserUtils.getInstance(context);
    }

    public static void init(Context context, String apiUrl){
        client = new OkHttpClient.Builder()
                .cookieJar(new SharedPreferencesCookieJar(context))
                .build();
        setUser(context);
        setBaseUrl(apiUrl);
        setSessionDetails(context);
    }

    public static Boolean sessionAvailable(){
        if (client==null){
            throw new RuntimeException("WEB_UTIL_ERROR: Didn't init WebUtil yet! Call WebUtil.init(context, url)");
        }
        if (baseApiUrl==null){
            throw new RuntimeException("WEB_UTIL_ERROR: baseApiUrl not set");
        }
        if (sessionDetails==null){
            throw new RuntimeException("WEB_UTIL_ERROR: sessionDetail not set");
        }
        if (sessionDetails.getString(SESSION_ID, null)==null){
            return false;
        }
        if (sessionDetails.getString(SESSION_EXPIRY_DATE, null)==null){
            return false;
        }
        // Parse the expiration date as a date object
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expiryDate;
        try {
            expiryDate = format.parse(sessionDetails.getString(SESSION_EXPIRY_DATE, ""));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Check if the session has expired
        if (expiryDate.getTime() < System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    public static Boolean isLoggedIn(){
        if (sessionAvailable() && userUtils.isLoggedIn()){
            return true;
        }
        return false;
    }

    public static void setLogIn(Boolean val){
        userUtils.setLogIn(val);
    }

    public static void setUserid(String user_id){
        userUtils.setUserid(user_id);
    }

    public static void setUsername(String username){
        userUtils.setUsername(username);
    }

    public static void clearSession(){
        SharedPreferences.Editor editor = sessionDetails.edit();
        editor.clear();
        editor.apply();
        userUtils.setLogIn(false);
    }

    public static void clearUser(){
        userUtils.clearUser();
    }
    public static void sendPost(String apiPath, Boolean requireLogin, JSONObject postJson, WebCallback callback) {
        // Create an implementation of the WebCallback interface
        WebCallback postAction = new WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                final MediaType mediaType = MediaType.get("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(String.valueOf(postJson), mediaType);
                String csrf_token = "";
                try {
                    csrf_token = json.getString("csrf_token");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d("SENDING POST", "Set-Cookie"+"sessionid=" + sessionDetails.getString(SESSION_ID, "")+", X-CSRFToken:"+csrf_token);
                Request request = new Request.Builder()
                        .url(baseApiUrl+apiPath)
                        .addHeader("Referer", baseApiUrl)
                        .addHeader("X-CSRFToken", csrf_token)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        String responseBody = response.body().string();
                        Log.d("POST-RESPONSE", "Response body: " + responseBody);
                        try {
                            // Response is json object
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (!jsonObject.has("success") || jsonObject.getBoolean("success")){
                                // success attribute is true
                                if (callback!=null){ callback.onSuccess(jsonObject);}
                            } else{
                                // success attribute is false
                                    if (callback!=null){ callback.onFailure(jsonObject);}
                            }
                        } catch (JSONException e) {
                            // Response is not json object
                            Log.e("JSON-ERROR", "Error parsing JSON: " + e.getMessage());
                                    if (callback!=null){ callback.onError(e);}
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle request error
                        Log.e("POST-ERROR", "Error making HTTP request: " + e.getMessage());
                        if (callback!=null){ callback.onError(e);}

                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                if (callback!=null) { callback.onError(t);}
            }

            @Override
            public void onFailure(JSONObject json) {
                if (callback!=null){ callback.onFailure(json);}
            }
        };

        // Must wrap post action with get_csrf because every post requires new csrf token
        WebUtils.WebCallback postWithCsrf = new WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                get_csrf(postAction);
            }

            @Override
            public void onError(Throwable t) {
                if (callback!=null) { callback.onError(t);}
            }

            @Override
            public void onFailure(JSONObject json) {
                if (callback!=null){ callback.onFailure(json);}
            }
        };
        if (requireLogin && !isLoggedIn()){
            userUtils.jumpToLogin();
            return;
        }
        if (sessionAvailable()){
            // Have session details, directly call post
            postWithCsrf.onSuccess(null);
        } else{
            // If no session id or expired, get new session id, then acquire csrf token and lastly post
            get_session(postWithCsrf);
        }
    }

    public static void sendPost(String apiPath, Boolean requireLogin, MultipartBody.Builder builder, WebCallback callback) {
        // Create an implementation of the WebCallback interface
        WebCallback postAction = new WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                String csrf_token = "";
                try {
                    csrf_token = json.getString("csrf_token");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d("SENDING POST", "Set-Cookie"+"sessionid=" + sessionDetails.getString(SESSION_ID, "")+", X-CSRFToken:"+csrf_token);
                Request request = new Request.Builder()
                        .url(baseApiUrl+apiPath)
                        .addHeader("Referer", baseApiUrl)
                        .addHeader("X-CSRFToken", csrf_token)
                        .post(builder.build())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        String responseBody = response.body().string();
                        Log.d("POST-RESPONSE", "Response body: " + responseBody);
                        try {
                            // Response is json object
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (!jsonObject.has("success") || jsonObject.getBoolean("success")){
                                // success attribute is true
                                if (callback!=null){ callback.onSuccess(jsonObject);}
                            } else{
                                // success attribute is false
                                if (callback!=null){ callback.onFailure(jsonObject);}
                            }
                        } catch (JSONException e) {
                            // Response is not json object
                            Log.e("JSON-ERROR", "Error parsing JSON: " + e.getMessage());
                            if (callback!=null){ callback.onError(e);}
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle request error
                        Log.e("POST-ERROR", "Error making HTTP request: " + e.getMessage());
                        if (callback!=null){ callback.onError(e);}

                    }
                });
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onFailure(JSONObject json) {

            }
        };

        // Must wrap post action with get_csrf because every post requires new csrf token
        WebUtils.WebCallback postWithCsrf = new WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                get_csrf(postAction);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onFailure(JSONObject json) {

            }
        };
        if (requireLogin && !isLoggedIn()){
            userUtils.jumpToLogin();
            return;
        }
        if (sessionAvailable()){
            // Have session details, directly call post
            postWithCsrf.onSuccess(null);
        } else{
            // If no session id or expired, get new session id, then acquire csrf token and lastly post
            get_session(postWithCsrf);
        }
    }

    public static void sendGet(String apiPath, Boolean requireLogin, WebCallback callback) {
        // Create an implementation of the WebCallback interface
        WebCallback getAction = new WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                // Create a Request object for the GET request
                //Log.d("SENDING GET", "Set-Cookie"+"sessionid=" + sessionDetails.getString(SESSION_ID, "")+", X-CSRFToken:"+sessionDetails.getString(CSRF_TOKEN, ""));
                Request request = new Request.Builder()
                        .url(baseApiUrl+apiPath)
                        //.addHeader("Set-Cookie", "sessionid=" + sessionDetails.getString(SESSION_ID, ""))
                        .build();

                // Use the OkHttpClient to create a Call object for the request
                Call call = client.newCall(request);

                // Use the enqueue method of the Call object to make the request asynchronously
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        String responseBody = response.body().string();
                        Log.d("GET-RESPONSE", "Response body: " + responseBody);
                        try {
                            // Response is json object
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (!jsonObject.has("success") || jsonObject.getBoolean("success")){
                                // success attribute is true
                                if (callback!=null){ callback.onSuccess(jsonObject);}
                            } else{
                                // success attribute is false
                                if (callback!=null){ callback.onFailure(jsonObject);}
                            }
                        } catch (JSONException e) {
                            // Response is not json object
                            Log.e("JSON-ERROR", "Error parsing JSON: " + e.getMessage());
                            if (callback!=null){ callback.onError(e);}
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle request error
                        Log.e("GET-ERROR", "Error making HTTP request: " + e.getMessage());
                        if (callback!=null){ callback.onError(e);}

                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                if (callback!=null){ callback.onError(t);}
            }

            @Override
            public void onFailure(JSONObject json) {
                if (callback!=null){ callback.onFailure(json);}
            }
        };

        if (requireLogin && !isLoggedIn()){
            userUtils.jumpToLogin();
            return;
        }
        if (sessionAvailable()){
            // Have session details, directly call getAction
            getAction.onSuccess(null);
        } else{
            // If no session id or expired, create a new session and then request get
            get_session(getAction);
        }


    }

    public static void get_session(WebCallback callback){
        // Create a Request object for the GET request
        Request request = new Request.Builder()
                .url(baseApiUrl+"/get_session/")
                .build();

        // Use the OkHttpClient to create a Call object for the request
        Call call = client.newCall(request);

        // Use the enqueue method of the Call object to make the request asynchronously
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response
                String responseData = response.body().string();
                Log.d("OkHttp", "Response: " + responseData);
                try {
                    // Response is json object
                    JSONObject json = new JSONObject(responseData);
                    SharedPreferences.Editor editor = sessionDetails.edit();
                    editor.putString(SESSION_ID, json.getString("session_id"));
                    editor.putString(SESSION_EXPIRY_DATE, json.getString("session_expiry_date"));
                    editor.apply();
                    if (callback!=null){ callback.onSuccess(json);}
                } catch (JSONException e) {
                    // Response is not json object
                    if (callback!=null){ callback.onError(e);}
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request error
                Log.e("OkHttp", "Error: " + e.getMessage());
                if (callback!=null){ callback.onError(e);}
            }
        });
    }

    public static void get_csrf(WebCallback callback){
        // Create a Request object for the GET request
        Request request = new Request.Builder()
                .url(baseApiUrl+"/get_csrf/")
                .build();

        // Use the OkHttpClient to create a Call object for the request
        Call call = client.newCall(request);

        // Use the enqueue method of the Call object to make the request asynchronously
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response
                String responseData = response.body().string();
                Log.d("OkHttp", "Response: " + responseData);
                try {
                    // Response is json object
                    JSONObject json = new JSONObject(responseData);
                    if (callback!=null){ callback.onSuccess(json);}
                } catch (JSONException e) {
                    // Response is not json object
                    if (callback!=null){ callback.onError(e); }
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request error
                Log.e("OkHttp", "Error: " + e.getMessage());
                if (callback!=null){ callback.onError(e); }
            }
        });
    }


}
