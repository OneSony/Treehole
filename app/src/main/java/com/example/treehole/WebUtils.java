package com.example.treehole;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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
                RequestBody body = RequestBody.create(mediaType, String.valueOf(postJson));
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
