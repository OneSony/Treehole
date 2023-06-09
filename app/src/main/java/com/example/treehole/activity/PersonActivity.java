package com.example.treehole.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
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
import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.WebUtils;
import com.example.treehole.paging.MomentPagingAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {

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

                    adapter=new MomentPagingAdapter(getApplication());

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

        ImageView profile_photo=findViewById(R.id.person_profile_photo);
        String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+user_id;
        Glide.with(getApplicationContext()).load(profile_photo_url).into(profile_photo);

        TextView textView=findViewById(R.id.person_username);
        textView.setText(username);


        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
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

        recyclerView=findViewById(R.id.person_recyclerview);

        recyclerView.setAdapter(adapter);
        //recyclerView.setItemAnimator(null);

        List<String> searchWords = new ArrayList<>();
        searchWords.add(username);

        viewModel.getPaging("username", searchWords).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        updateUsername();
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
}