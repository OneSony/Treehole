package com.example.treehole.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.treehole.R;
import com.example.treehole.utils.WebUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            /*if(msg.what==-1){
                Toast.makeText(getApplicationContext(),"WRONG",Toast.LENGTH_SHORT).show();
                register_button.setEnabled(true);
            }else if(msg.what==0){
            }else if(msg.what==1){//登录
            }else if(msg.what==2){
            }else if(msg.what==3) {//注册
                Gson gson = new Gson();
                JsonObject obj = gson.fromJson((String) msg.obj, JsonObject.class);

                if (obj.get("success").getAsBoolean()) {
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences mPreferences = getSharedPreferences("com.example.android.Treehole", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.putBoolean("LOGIN_SIT", true);
                    preferencesEditor.apply();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    //register_button.setEnabled(true);
                } else {
                    if(obj.get("message").getAsString()==""){
                        Toast.makeText(getApplicationContext(),"失败 "+obj.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    }else if(obj.get("message").getAsString()==""){

                    }else if(obj.get("message").getAsString()==""){

                    }else{
                        Toast.makeText(getApplicationContext(),"未知错误",Toast.LENGTH_SHORT).show();
                    }
                    register_button.setEnabled(true);
                    username_box.setEnabled(true);
                    password_box.setEnabled(true);
                }
            }*/

            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(),"注册成功，请登录",Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent().putExtra("USERNAME", username_box.getEditText().getText().toString());
                    setResult(0, intent);

                    finish();
                    break;
                case -1:
                    Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
                    register_button.setEnabled(true);
                    username_box.setEnabled(true);
                    password_box.setEnabled(true);
                    break;
            }
        }
    };

    TextInputLayout username_box,password_box;
    Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setSupportActionBar(findViewById(R.id.register_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("注册");

        username_box = findViewById(R.id.r_username_box);
        password_box = findViewById(R.id.r_password_box);


        register_button = findViewById(R.id.register_button);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reg_click(View view) {

        username_box.setErrorEnabled(false);
        password_box.setErrorEnabled(false);

        String username = username_box.getEditText().getText().toString();
        String password = password_box.getEditText().getText().toString();

        int username_sit = username_check(username);
        int password_sit = password_check(password);

        if(!(username_sit==0&&password_sit==0)){
            return;
        }

        register_button.setEnabled(false);

        username_box.setEnabled(false);
        password_box.setEnabled(false);


        WebUtils.WebCallback callback = new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                try{
                    Boolean success = json.getBoolean("success");
                    String message = json.getString("message");
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
        WebUtils.sendPost("/users/signup/", false, json, callback);

/*
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
}