package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.R;
import com.example.treehole.adapter.SearchUserListAdapter;
import com.example.treehole.room.SearchUserResult;
import com.example.treehole.utils.UserUtils;
import com.example.treehole.utils.WebUtils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FollowerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchUserListAdapter adapter;

    private TextView noDataTextView;

    private ProgressBar progressBar;

    private int searchType = 0;//0是关注，1是粉丝，2是黑名单


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);


        searchType=getIntent().getIntExtra("SEARCH_TYPE",0);


        noDataTextView=findViewById(R.id.follower_no_data);
        noDataTextView.setVisibility(View.GONE);

        progressBar=findViewById(R.id.follower_progress);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(findViewById(R.id.follower_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("人际");


        TabLayout tabLayout = findViewById(R.id.follower_tab);
        TabLayout.Tab tab = tabLayout.getTabAt(searchType);
        if (tab != null) {
            tab.select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                noDataTextView.setVisibility(View.GONE);
                // 当选项卡被选中时调用此方法
                // 获取选中标签的文本
                int tabPosition = tab.getPosition();

                Log.d("Search Type", "tabPosition="+tabPosition);

                searchType=tabPosition;

                progressBar.setVisibility(View.VISIBLE);
                noDataTextView.setVisibility(View.GONE);

                sendGet(UserUtils.getUserid());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 当选项卡取消选择时调用此方法
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 当选项卡再次被选中时调用此方法
            }
        });



        recyclerView=findViewById(R.id.follower_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        adapter=new SearchUserListAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new SearchUserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String user_id,String username) throws ExecutionException, InterruptedException {
                //Intent intent = new Intent(getApplicationContext(), PersonActivity.class);


                Intent intent = new Intent(FollowerActivity.this, PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("USER_ID", user_id);
                intent.putExtra("BUNDLE_DATA", bundle);
                FollowerActivity.this.startActivity(intent);


            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //刚进来的时候就要提交搜索
        noDataTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //List<SearchUserResult> searchUserResults=new ArrayList<>();
        //searchAndInsert(searchType,"yun", searchUserResults, adapter);

        sendGet(UserUtils.getUserid());


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


    private void sendGet(String user_id){
        adapter.setSearchUserResults(new ArrayList<>());
        adapter.notifyDataSetChanged();

        String api="";

        if(searchType==0){
            api="/users/follow?id=";
        }else if(searchType==1){
            api="/users/follower?id=";
        }else if(searchType==2){
            api="/users/blacklist?id=";
        }else{
            return;
        }
        WebUtils.sendGet(api+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {



                List<SearchUserResult> newUsers=new ArrayList<>();

                Log.d("COMMENT SUCC",json.toString());

                try {

                    Object msg=json.get("message");

                    if (msg instanceof JSONArray) {
                        JSONArray nestedArray = (JSONArray) msg;

                        // 处理列表中的数据
                        for (int ii = 0; ii < nestedArray.length(); ii++) {
                            Object listItem = nestedArray.get(ii);
                            if(listItem instanceof JSONObject){
                                JSONObject comment=(JSONObject)listItem;
                                String user_id=comment.getString("user_id");
                                String username=comment.getString("username");
                                String profile_picture=comment.getString("profile_picture");
                                newUsers.add(new SearchUserResult(user_id,username));

                                Log.d("COMMENT GET",user_id+" "+"username");
                            }
                        }
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(newUsers.size()==0) {
                            progressBar.setVisibility(View.GONE);
                            noDataTextView.setVisibility(View.VISIBLE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            adapter.setSearchUserResults(newUsers);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }

            @Override
            public void onError(Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

