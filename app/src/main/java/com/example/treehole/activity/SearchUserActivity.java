package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.SearchUserListAdapter;
import com.example.treehole.SearchUserResult;
import com.example.treehole.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        setSupportActionBar(findViewById(R.id.search_user_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        String query=getIntent().getStringExtra("QUERY");


        recyclerView=findViewById(R.id.search_user_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        adapter=new SearchUserListAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new SearchUserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String user_id,String username) throws ExecutionException, InterruptedException {
                //Intent intent = new Intent(getApplicationContext(), PersonActivity.class);

                Intent intent = new Intent(getApplicationContext(), MsgActivity.class);
                Bundle bundle = new Bundle();


                bundle.putSerializable("DATA",viewModel.searchMessage(user_id,username));
                intent.putExtra("BUNDLE_DATA",bundle);

                /*
                bundle.putString("USERNAME", user_id);
                intent.putExtra("BUNDLE_DATA", bundle);*/
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        //测试数据！！！！
        List<SearchUserResult> searchUserResults=new ArrayList<>();
        /*searchUserResults.add(new SearchUserResult(query,query+"'s username","?"));

        adapter.setSearchUserResults(searchUserResults);
        adapter.notifyDataSetChanged();*/
        searchAndInsert(query, searchUserResults, adapter);



        SearchView searchView = findViewById(R.id.search_user_searchview);
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件

                //测试数据！！！！
                List<SearchUserResult> searchUserResults=new ArrayList<>();
                //searchUserResults.add(new SearchUserResult(query,query+"'s username","?"));
                searchAndInsert(query, searchUserResults, adapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 处理搜索框文本变化事件
                // 这里可以根据 newText 进行实时搜索或过滤操作
                return true;
            }
        });
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

    private void searchAndInsert(String query, List<SearchUserResult> listContainer, SearchUserListAdapter adapter) {
        Log.d("GET", "HEERE");
        WebUtils.sendGet("/users/find_users?username="+query, false, new WebUtils.WebCallback() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    JSONArray users = json.getJSONArray("message");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String user_id = user.getString("user_id");
                        String username = user.getString("username");
                        String profile_picture = user.isNull("profile_picture") ? "" : user.getString("profile_picture");
                        listContainer.add(new SearchUserResult(user_id, username, profile_picture));
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.setSearchUserResults(listContainer);
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    Log.e("SEARCHUSERACTIVITY", "ERROR: "+e.getMessage());
                }

            }

            @Override
            public void onError(Throwable t) {
                Log.e("SEARCHUSERACTIVITY", "ERROR: "+t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {

                Log.e("SEARCHUSERACTIVITY", "FAILURE: "+json.optString("message", "onFailure"));
            }
        });
    }
}