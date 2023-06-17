package com.example.treehole.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.treehole.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private static String baseApiUrl = "https://rickyvu.pythonanywhere.com";

    private static Context context;

    public interface WebCallback{
        void onSuccess(JSONObject json);
        void onError(Throwable t);
        void onFailure(JSONObject json);
    }

    public static void init(Context context){
        client = new OkHttpClient.Builder()
                .cookieJar(new SharedPreferencesCookieJar(context))
                .build();
        WebUtils.context = context;
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
                                // response with unauthorized code
                                if (response.code() == 401) {
                                    Log.d("sendPost", "POST request failed due to unauthorized access");
                                    WebUtils.jumpToLogin();
                                } else {
                                    if (callback!=null){ callback.onFailure(jsonObject);}
                                }

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
        postWithCsrf.onSuccess(null);
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
                                // response with unauthorized code
                                if (response.code() == 401) {
                                    Log.d("sendPost", "POST request failed due to unauthorized access");
                                    WebUtils.jumpToLogin();
                                } else {
                                    if (callback!=null){ callback.onFailure(jsonObject);}
                                }
                                response.close();
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

            }

            @Override
            public void onFailure(JSONObject json) {

            }
        };
        postWithCsrf.onSuccess(null);
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
                                // response with unauthorized code
                                if (response.code() == 401) {
                                    Log.d("sendPost", "POST request failed due to unauthorized access");
                                    WebUtils.jumpToLogin();
                                } else {
                                    if (callback!=null){ callback.onFailure(jsonObject);}
                                }
                                response.close();
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
        getAction.onSuccess(null);
    }

    public static void sendDelete(String apiPath, Boolean requireLogin, JSONObject postJson, WebCallback callback) {
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
                Request request = new Request.Builder()
                        .url(baseApiUrl+apiPath)
                        .addHeader("Referer", baseApiUrl)
                        .addHeader("X-CSRFToken", csrf_token)
                        .delete(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        String responseBody = response.body().string();
                        Log.d("DELETE-RESPONSE", "Response body: " + responseBody);
                        try {
                            // Response is json object
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (!jsonObject.has("success") || jsonObject.getBoolean("success")){
                                // success attribute is true
                                if (callback!=null){ callback.onSuccess(jsonObject);}
                            } else{
                                // success attribute is false
                                // response with unauthorized code
                                if (response.code() == 401) {
                                    Log.d("sendPost", "DELETE request failed due to unauthorized access");
                                    WebUtils.jumpToLogin();
                                } else {
                                    if (callback!=null){ callback.onFailure(jsonObject);}
                                }

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
                        Log.e("DELETE-ERROR", "Error making HTTP request: " + e.getMessage());
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
        WebUtils.WebCallback deleteWithCsrf = new WebCallback() {
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
        deleteWithCsrf.onSuccess(null);
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

    public static void jumpToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
