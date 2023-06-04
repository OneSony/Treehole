package com.example.treehole;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SearchUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        setSupportActionBar(findViewById(R.id.search_user_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        String query=getIntent().getStringExtra("QUERY");
        if (query != null) {
            TextView textView=findViewById(R.id.test_textview);
            textView.setText(query);
        }

        SearchView searchView = findViewById(R.id.search_user_searchview);
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件
                TextView textView=findViewById(R.id.test_textview);
                textView.setText(query);
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