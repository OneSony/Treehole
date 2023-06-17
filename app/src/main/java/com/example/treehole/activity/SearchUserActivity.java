package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.R;
import com.example.treehole.adapter.SearchUserListAdapter;
import com.example.treehole.room.SearchUserResult;
import com.example.treehole.utils.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserListAdapter adapter;

    private TextView noDataTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        noDataTextView=findViewById(R.id.search_no_data);
        noDataTextView.setVisibility(View.GONE);

        ProgressBar progressBar=findViewById(R.id.search_progress);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(findViewById(R.id.search_user_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("搜索用户");

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
                intent.putExtra("FROM",0);

                /*
                bundle.putString("USERNAME", user_id);
                intent.putExtra("BUNDLE_DATA", bundle);*/
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //刚进来的时候就要提交搜索
        noDataTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);



        List<SearchUserResult> searchUserResults=new ArrayList<>();
        searchAndInsert(query, searchUserResults, adapter);



        SearchView searchView = findViewById(R.id.search_user_searchview);
        searchView.setQuery(query, false);
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();
                // 处理搜索提交事件
                noDataTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                adapter.setSearchUserResults(new ArrayList<>());
                adapter.notifyDataSetChanged();

                List<SearchUserResult> searchUserResults=new ArrayList<>();
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
                    if (users.length() == 0) {
                        Log.d("ADD","NODATA");

                        runOnUiThread(new Runnable() {
                            public void run() {

                                ProgressBar progressBar=findViewById(R.id.search_progress);
                                progressBar.setVisibility(View.GONE);

                                noDataTextView.setVisibility(View.VISIBLE);
                            }
                        });

                    }else {
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            String user_id = user.getString("user_id");
                            String username = user.getString("username");
                            listContainer.add(new SearchUserResult(user_id, username));
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {

                                ProgressBar progressBar=findViewById(R.id.search_progress);
                                progressBar.setVisibility(View.GONE);
                                adapter.setSearchUserResults(listContainer);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (JSONException e) {
                    Log.e("SEARCHUSERACTIVITY", "ERROR: "+e.getMessage());
                }

            }

            @Override
            public void onError(Throwable t) {
                //progressBar.setVisibility(View.GONE);
                Log.e("SEARCHUSERACTIVITY", "ERROR: "+t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                //progressBar.setVisibility(View.GONE);
                Log.e("SEARCHUSERACTIVITY", "FAILURE: "+json.optString("message", "onFailure"));
            }
        });
    }
}