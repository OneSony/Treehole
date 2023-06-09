package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    //private String sharedPrefFile ="com.example.android.Treehole";
    private String csrf_token;
    //private SharedPreferences mPreferences;
    TextInputLayout username_box,password_box;

    Button login_button,to_register_button;

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
                    login();
                }
                return true;
            }
        });

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

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
        Request request = new Request.Builder().url("https://rickyvu.pythonanywhere.com/users/login/").post(requestBody).build();


        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what=-1;
                //handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what=1;

                    Log.d("LOGIN",msg.obj.toString());
                    //Log.d("HTTP",response.body().toString());
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WebUtils.setLogIn(true);
                            WebUtils.setUserid(user_id);
                            WebUtils.setUsername(username);
                            Toast.makeText(getApplicationContext(),"登录成功"+username,Toast.LENGTH_SHORT).show();
                            //new UserUtils.RegisterForPushNotificationsAsync(LoginActivity.this).execute();
                            intent_to_main();
                        }
                    });*/
                }else{
                    Message msg = new Message();
                    msg.what=2;
                    //handler.sendMessage(msg);
                }

            }


        });

/*
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
        });*/
    }

    public void login_click(View view) throws IOException {
        login();
    }

    public void reg_click(View view) throws IOException {

        username_box.setErrorEnabled(false);
        password_box.setErrorEnabled(false);

        Intent intent = new Intent(this, RegisterActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, 0);
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