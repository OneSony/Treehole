package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        setSupportActionBar(findViewById(R.id.search_user_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        String query=getIntent().getStringExtra("QUERY");


        recyclerView=findViewById(R.id.search_user_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        adapter=new SearchUserListAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new SearchUserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String username) {
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                intent.putExtra("BUNDLE_DATA", bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        //测试数据！！！！
        List<SearchUserResult> searchUserResults=new ArrayList<>();
        searchUserResults.add(new SearchUserResult(query+"1","?"));
        searchUserResults.add(new SearchUserResult(query+"2","?"));
        searchUserResults.add(new SearchUserResult(query+"3","?"));

        adapter.setSearchUserResults(searchUserResults);
        adapter.notifyDataSetChanged();



        SearchView searchView = findViewById(R.id.search_user_searchview);
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件

                //测试数据！！！！
                List<SearchUserResult> searchUserResults=new ArrayList<>();
                searchUserResults.add(new SearchUserResult(query+"1","?"));
                searchUserResults.add(new SearchUserResult(query+"2","?"));
                searchUserResults.add(new SearchUserResult(query+"3","?"));
                adapter.setSearchUserResults(searchUserResults);
                adapter.notifyDataSetChanged();
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
}