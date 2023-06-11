package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.UserUtils;
import com.example.treehole.WebUtils;
import com.example.treehole.paging.MomentPagingAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PersonActivity extends AppCompatActivity {

    private boolean isFollowed=false;
    private boolean isBlacklisted=false;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // 处理消息并更新UI
            switch (msg.what) {
                case 20:
                    TextView textView=findViewById(R.id.person_username);
                    textView.setText(username);

                    List<String> searchWords = new ArrayList<>();
                    searchWords.add(username);

                    adapter=new MomentPagingAdapter(PersonActivity.this,false);

                    adapter.addLoadStateListener(loadStates-> {
                        if (loadStates.getRefresh() instanceof LoadState.Loading) {
                            //progressBar.setVisibility(View.VISIBLE);
                            // 数据源正在加载中
                            // 可以显示加载中的动画或提示信息
                        } else if (loadStates.getRefresh() instanceof LoadState.Error) {
                            //progressBar.setVisibility(View.GONE);
                            // 数据源加载时遇到错误
                            // 可以显示错误提示信息
                        } else if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                            //progressBar.setVisibility(View.GONE);

                            Log.d("PAGING","count"+adapter.getItemCount()+loadStates.getRefresh().getEndOfPaginationReached());
                            if(adapter.getItemCount()==0) {
                                //noDataTextView.setVisibility(View.VISIBLE);
                            }

                        }
                        return null;
                    });

                    recyclerView.setAdapter(adapter);

                    viewModel.getPaging("username", searchWords).observe(PersonActivity.this,
                            dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    break;
                case 70:
                    if(follower_count !=null&&msg.obj!=null){
                        follower_count.setText(String.valueOf(msg.obj));
                    }
                    break;
                case 80:
                    if(follow_count !=null&&msg.obj!=null){
                        follow_count.setText(String.valueOf(msg.obj));
                    }
                    break;

                case 90:
                    if(followButton !=null&&msg.obj!=null){
                        if((boolean)msg.obj==true){
                            followButton.setText("已关注");
                        }else{
                            followButton.setEnabled(true);
                        }
                    }
                    break;

                case 100:
                    if(blacklistButton !=null&&msg.obj!=null){
                        if((boolean)msg.obj==true){
                            blacklistButton.setText("已拉黑");;
                        }else{
                            blacklistButton.setEnabled(true);
                        }
                    }
                    break;

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // 移除未处理的消息和处理程序
    }

    private String username;
    private String user_id;

    private MomentPagingAdapter adapter;

    private RecyclerView recyclerView;

    private SearchViewModel viewModel;

    private TextView follow_count;
    private TextView follower_count;

    Button msgButton,followButton,blacklistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Bundle bundle=getIntent().getBundleExtra("BUNDLE_DATA");
        if (bundle != null) {
             username = bundle.getString("USERNAME");
             user_id=bundle.getString("USER_ID");
        }

        setSupportActionBar(findViewById(R.id.person_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(username);


        msgButton=findViewById(R.id.person_msg_button);
        msgButton.setEnabled(false);

        followButton=findViewById(R.id.person_follow_button);
        followButton.setEnabled(false);

        blacklistButton=findViewById(R.id.person_blacklist_button);
        blacklistButton.setEnabled(false);


        follow_count = findViewById(R.id.person_follow_count);
        follow_count.setText("-");

        follower_count = findViewById(R.id.person_follower_count);
        follower_count.setText("-");


        ImageView profile_photo=findViewById(R.id.person_profile_photo);
        String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+user_id;
        Glide.with(getApplicationContext()).load(profile_photo_url).into(profile_photo);

        TextView textView=findViewById(R.id.person_username);
        textView.setText(username);


        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);


        adapter=new MomentPagingAdapter(this,false);

        adapter.addLoadStateListener(loadStates-> {
            if (loadStates.getRefresh() instanceof LoadState.Loading) {
                //progressBar.setVisibility(View.VISIBLE);
                // 数据源正在加载中
                // 可以显示加载中的动画或提示信息
            } else if (loadStates.getRefresh() instanceof LoadState.Error) {
                //progressBar.setVisibility(View.GONE);
                // 数据源加载时遇到错误
                // 可以显示错误提示信息
            } else if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                //progressBar.setVisibility(View.GONE);

                Log.d("PAGING","count"+adapter.getItemCount()+loadStates.getRefresh().getEndOfPaginationReached());
                if(adapter.getItemCount()==0) {
                    //noDataTextView.setVisibility(View.VISIBLE);
                }

            }
            return null;
        });

        recyclerView=findViewById(R.id.person_recyclerview);

        recyclerView.setAdapter(adapter);
        //recyclerView.setItemAnimator(null);

        List<String> searchWords = new ArrayList<>();
        searchWords.add(username);

        viewModel.getPaging("username", searchWords).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        updateUsername();

        WebUtils.sendGet("/users/description?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                Log.d("description",json.toString());
                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    String description = responseJson.optString("description", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView descriptionTextView=findViewById(R.id.person_about);
                            descriptionTextView.setText(description);
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        WebUtils.sendGet("/users/follow_count?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    Integer follow_count = responseJson.optInt("count", 0);
                    Log.d("SUCCESS", String.valueOf(follow_count));


                    Message msg=new Message();
                    msg.what=80;
                    msg.obj=follow_count;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        WebUtils.sendGet("/users/follower_count?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    Integer follower_count = responseJson.optInt("count", 0);
                    Log.d("SUCCESS", String.valueOf(follower_count));


                    Message msg=new Message();
                    msg.what=70;
                    msg.obj=follower_count;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });





        if(user_id.equals(UserUtils.getUserid())){//我自己
            msgButton.setEnabled(false);
            followButton.setEnabled(false);
            blacklistButton.setEnabled(false);
        }else{
            msgButton.setEnabled(true);
            isFollowed();
            isBlacklisted();

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // android.R.id.home 这个是获取ids.xml页面的返回箭头，项目自带的，要加上android
            case android.R.id.home:
                // 返回
                this.finish();
                // 结束
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUsername(){

        if(user_id!=null) {
            WebUtils.sendGet("/users/username?id=" + user_id, false, new WebUtils.WebCallback() {
                @Override
                public void onSuccess(JSONObject json) {

                    String new_username = "";
                    try {
                        JSONObject msg = json.getJSONObject("message");
                        new_username = msg.getString("username");
                        Log.d("SUCCESS", json.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if(!new_username.equals(username)) {
                        username = new_username;
                    }

                    Message msg = new Message();
                    msg.what=20;
                    handler.sendMessage(msg);
                }

                @Override
                public void onError(Throwable t) {
                    Log.e("ERROR", t.getMessage());
                }

                @Override
                public void onFailure(JSONObject json) {
                    try {
                        Log.e("FAILURE", json.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void person_msg_click(View view) throws ExecutionException, InterruptedException {

        Intent intent = new Intent(PersonActivity.this, MsgActivity.class);
        Bundle bundle = new Bundle();

        ChatViewModel viewModel = new ViewModelProvider(PersonActivity.this).get(ChatViewModel.class);


        bundle.putSerializable("DATA",viewModel.searchMessage(user_id,username));
        intent.putExtra("BUNDLE_DATA",bundle);
        intent.putExtra("FROM",1);
                /*
                bundle.putString("USERNAME", user_id);
                intent.putExtra("BUNDLE_DATA", bundle);*/
        startActivity(intent);

    }

    public void person_blacklist_click(View view) {
        //blacklistButton.setEnabled(false);
        if(isBlacklisted==false) {
            sendPost(0);
        }else{
            sendDelete(0);
        }
    }

    public void person_follow_click(View view) {
        //followButton.setEnabled(false);
        if(isFollowed==false) {
            sendPost(1);
        }else{
            sendDelete(1);
        }
    }

    private void sendPost(int flag){

        String api="";


        if(flag==0){
            api="/users/blacklist/";
        }else if(flag==1) {
            api = "/users/follow/";
        }else{
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("BLACKLIST","json"+json.toString());
        WebUtils.sendPost(api, true, json, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(flag==0){
                                isBlacklisted=true;
                                blacklistButton.setText("已拉黑");
                                blacklistButton.setEnabled(true);
                            }else if(flag==1) {
                                isFollowed=true;
                                followButton.setText("已关注");
                                followButton.setEnabled(true);
                                WebUtils.sendGet("/users/follower_count?id="+user_id, false, new WebUtils.WebCallback() {
                                    @Override
                                    public void onSuccess(JSONObject json) {

                                        try {
                                            JSONObject responseJson=json.getJSONObject("message");
                                            Integer follower_count = responseJson.optInt("count", 0);
                                            Log.d("SUCCESS", String.valueOf(follower_count));


                                            Message msg=new Message();
                                            msg.what=70;
                                            msg.obj=follower_count;
                                            handler.sendMessage(msg);

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        Log.e("ERROR", t.getMessage());
                                    }

                                    @Override
                                    public void onFailure(JSONObject json) {
                                        try {
                                            Log.e("FAILURE", json.getString("message"));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }else{
                                return;
                            }
                        }
                    });
                    Log.d("SUCCESS", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendDelete(int flag){

        String api="";

        if(flag==0){
            api="/users/blacklist/";
        }else if(flag==1) {
            api = "/users/follow/";
        }else{
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("BLACKLIST","json"+json.toString());
        WebUtils.sendDelete(api, true, json, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                try {

                    Log.d("SUCCESS", json.getString("message"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(flag==0){
                                isBlacklisted=false;
                                blacklistButton.setText("拉黑");
                                blacklistButton.setEnabled(true);
                            }else if(flag==1) {
                                isFollowed=false;
                                followButton.setText("关注");
                                followButton.setEnabled(true);
                                WebUtils.sendGet("/users/follower_count?id="+user_id, false, new WebUtils.WebCallback() {
                                    @Override
                                    public void onSuccess(JSONObject json) {

                                        try {
                                            JSONObject responseJson=json.getJSONObject("message");
                                            Integer follower_count = responseJson.optInt("count", 0);
                                            Log.d("SUCCESS", String.valueOf(follower_count));


                                            Message msg=new Message();
                                            msg.what=70;
                                            msg.obj=follower_count;
                                            handler.sendMessage(msg);

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        Log.e("ERROR", t.getMessage());
                                    }

                                    @Override
                                    public void onFailure(JSONObject json) {
                                        try {
                                            Log.e("FAILURE", json.getString("message"));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }else{
                                return;
                            }
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void isFollowed(){
        WebUtils.sendGet("/users/is_following?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    boolean following = responseJson.optBoolean("yes", false);
                    Log.d("SUCCESS", String.valueOf(following));

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            isFollowed=following;

                            if(followButton!=null) {
                                if (following) {
                                    followButton.setText("已关注");
                                    followButton.setEnabled(true);
                                } else {
                                    followButton.setEnabled(true);
                                }
                            }
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void isBlacklisted(){
        WebUtils.sendGet("/users/is_blacklisted?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    boolean blacklisting = responseJson.optBoolean("yes", false);
                    Log.d("SUCCESS", String.valueOf(blacklisting));

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            isBlacklisted=blacklisting;

                            if(blacklistButton!=null) {
                                if (blacklisting) {
                                    blacklistButton.setText("已拉黑");
                                    blacklistButton.setEnabled(true);
                                } else {
                                    blacklistButton.setEnabled(true);
                                }
                            }
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}