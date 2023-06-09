package com.example.treehole.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.treehole.R;
import com.example.treehole.UserUtils;
import com.example.treehole.WebUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.pushy.sdk.Pushy;

public class LoginActivity extends AppCompatActivity {
    //private String sharedPrefFile ="com.example.android.Treehole";
    private String csrf_token;
    //private SharedPreferences mPreferences;
    TextInputLayout username_box,password_box;

    Button login_button,to_register_button;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            /*if(msg.what==-1){
                Toast.makeText(getApplicationContext(),"WRONG",Toast.LENGTH_SHORT).show();
                login_button.setEnabled(true);
                to_register_button.setEnabled(true);
            }else if(msg.what==0){
                Gson gson= new Gson();
                JsonElement element= gson.fromJson(String.valueOf(msg.obj),JsonElement.class);
                JsonObject obj= element.getAsJsonObject();
                csrf_token= obj.get("csrf_token").getAsString();
                Toast.makeText(getApplicationContext(),csrf_token,Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("CSRF_TOKEN", csrf_token);
                preferencesEditor.apply();

                Boolean logic_sit = mPreferences.getBoolean("LOGIN_SIT", false);
                if(logic_sit==true){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }else if(msg.what==1){//登录
                Gson gson= new Gson();
                JsonObject obj = gson.fromJson((String)msg.obj, JsonObject.class);
                //Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                if(obj.get("success").getAsBoolean()){
                    Toast.makeText(getApplicationContext(),"成功",Toast.LENGTH_SHORT).show();
                    intent_to_main();
                }else{
                    if(obj.get("message").getAsString()==""){
                        Toast.makeText(getApplicationContext(),"失败 "+obj.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    }else if(obj.get("message").getAsString()==""){

                    }else if(obj.get("message").getAsString()==""){

                    }else{
                        Toast.makeText(getApplicationContext(),"未知错误",Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getApplicationContext(),"失败",Toast.LENGTH_SHORT).show();
                    login_button.setEnabled(true);
                    to_register_button.setEnabled(true);
                    username_box.setEnabled(true);
                    password_box.setEnabled(true);
                }
            }else if(msg.what==2){
                Toast.makeText(getApplicationContext(),"登入失败",Toast.LENGTH_SHORT).show();
                login_button.setEnabled(true);
                to_register_button.setEnabled(true);
                username_box.setEnabled(true);
                password_box.setEnabled(true);
            }*/

            switch (msg.what){
                case 0:
                    WebUtils.setLogIn(true);
                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                    //new UserUtils.RegisterForPushNotificationsAsync(LoginActivity.this).execute();
                    intent_to_main();
                    break;
                case -1:
                    Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                    login_button.setEnabled(true);
                    to_register_button.setEnabled(true);
                    username_box.setEnabled(true);
                    password_box.setEnabled(true);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        WebUtils.init(getApplicationContext(),"https://rickyvu.pythonanywhere.com");
        Pushy.listen(this);
        new UserUtils.RegisterForPushNotificationsAsync(this).execute();
        setContentView(R.layout.activity_login);

        setSupportActionBar(findViewById(R.id.login_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setTitle("Login");

        if(WebUtils.isLoggedIn()){
            intent_to_main();
        }

        //mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        //if(mPreferences.getBoolean("LOGIN_SIT", false)){
        //    intent_to_main();
        //}

        username_box = findViewById(R.id.username_box);
        password_box = findViewById(R.id.password_box);

        login_button = findViewById(R.id.login_button);
        to_register_button = findViewById(R.id.to_register_button);


        password_box.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {   // 按下完成按钮，这里和上面imeOptions对应
                    //login();
                }
                return true;
            }
        });

/*
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://rickyvu.pythonanywhere.com/initiate").get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP","not OK");

                Message msg = new Message();
                msg.what=-1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what=0;
                msg.obj=response.body().string();
                handler.sendMessage(msg);

            }
        });
*/
    }

    public void login(){
        username_box.setErrorEnabled(false);
        password_box.setErrorEnabled(false);

        String username = username_box.getEditText().getText().toString();
        String password = password_box.getEditText().getText().toString();

        int username_sit = username_check(username);
        int password_sit = password_check(password);

        if(!(username_sit==0&&password_sit==0)){
            return;
        }

        login_button.setEnabled(false);
        to_register_button.setEnabled(false);

        username_box.setEnabled(false);
        password_box.setEnabled(false);

        WebUtils.WebCallback callback = new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                try{
                    Boolean success = json.getBoolean("success");
                    String message = json.getString("message");
                    WebUtils.setLogIn(true);
                    Log.d("JSON-PARSED", "success: " + String.valueOf(success) + ", message: " + message);

                    Message msg = new Message();
                    msg.what=0;
                    handler.sendMessage(msg);

                } catch (JSONException e){
                    Log.e("JSON-ERROR", "Error parsing JSON: " + e.getMessage());

                    Message msg = new Message();
                    msg.what=-1;
                    handler.sendMessage(msg);

                }

            }

            @Override
            public void onError(Throwable t) {
                Log.e("OkHttp", "Error: " + t.getMessage());
                Log.e("OkHttp", "?");

                Message msg = new Message();
                msg.what=-1;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(JSONObject json) {

                Message msg = new Message();
                msg.what=-1;
                handler.sendMessage(msg);
            }
        };
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebUtils.sendPost("/users/login/", false, json, callback);

    }

    public void login_click(View view) throws IOException {

        //login();

        username_box.setErrorEnabled(false);
        password_box.setErrorEnabled(false);

        String username = username_box.getEditText().getText().toString();
        String password = password_box.getEditText().getText().toString();

        int username_sit = username_check(username);
        int password_sit = password_check(password);

        if(!(username_sit==0&&password_sit==0)){
            return;
        }

        login_button.setEnabled(false);
        to_register_button.setEnabled(false);

        username_box.setEnabled(false);
        password_box.setEnabled(false);

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
        Request request = new Request.Builder().url("https://rickyvu.pythonanywhere.com/users/login/").post(requestBody).build();


        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what=-1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what=1;
                    Log.d("HTTP",response.body().toString());

                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what=2;
                    handler.sendMessage(msg);
                }

            }
        });*/


        WebUtils.sendPost("/users/login/", false, json, new WebUtils.WebCallback() {

            @Override
            public void onSuccess(JSONObject json) {
                JSONObject login_json = null;
                try {
                    login_json = json.getJSONObject("message");
                    String user_id = login_json.getString("id");

                    Log.d("Login get user_id", user_id);

                    //run in UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WebUtils.setLogIn(true);
                            WebUtils.setUserid(user_id);
                            WebUtils.setUsername(username);
                            Toast.makeText(getApplicationContext(),"登录成功"+username,Toast.LENGTH_SHORT).show();
                            //new UserUtils.RegisterForPushNotificationsAsync(LoginActivity.this).execute();
                            intent_to_main();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.d("POSTRETRIEVE", t.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                        login_button.setEnabled(true);
                        to_register_button.setEnabled(true);
                        username_box.setEnabled(true);
                        password_box.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFailure(JSONObject json) {
                Log.d("POSTRETRIEVE", json.optString("message", "onFailure"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                        login_button.setEnabled(true);
                        to_register_button.setEnabled(true);
                        username_box.setEnabled(true);
                        password_box.setEnabled(true);
                    }
                });
            }
        });
    }

    public void reg_click(View view) throws IOException {

        username_box.setErrorEnabled(false);
        password_box.setErrorEnabled(false);

        Intent intent = new Intent(this, RegisterActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, 0);

        /*String username = username_box.getEditText().getText().toString();
        String password = password_box.getEditText().getText().toString();

        if(username.equals("")||password.equals("")){
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
        Request request = new Request.Builder().url("https://rickyvu.pythonanywhere.com/users/signup/").post(requestBody).build();

        Call call = client.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what=-1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what=3;
                    //Log.d("HTTP",response.body().toString());
                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what=-1;
                    handler.sendMessage(msg);
                }
            }
        });*/
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(data!=null){
             if(requestCode==0) {
                 String username=data.getStringExtra("USERNAME");
                 username_box.getEditText().setText(username);
                 password_box.getEditText().getText().clear();
             }
         }
     }

    private void intent_to_main(){
        //mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        //SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        //preferencesEditor.putBoolean("LOGIN_SIT", true);
        //preferencesEditor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private int password_check(String str){
        if(str.equals("")){
            password_box.setError("密码不能为空");
            return -1;
        }
        return 0;
    }

    private int username_check(String str){

        if(str.equals("")){
            username_box.setError("账号不能为空");
            return -1;
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void temp_click(View view) {
        intent_to_main();
    }
}