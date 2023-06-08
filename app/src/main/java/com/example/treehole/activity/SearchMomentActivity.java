package com.example.treehole.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.paging.MomentPagingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchMomentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MomentPagingAdapter adapter;

    private TextView noDataTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_moment);


        noDataTextView=findViewById(R.id.search_no_data_moment);
        noDataTextView.setVisibility(View.GONE);

        ProgressBar progressBar=findViewById(R.id.search_progress_moment);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(findViewById(R.id.search_moment_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        String query=getIntent().getStringExtra("QUERY");


        recyclerView=findViewById(R.id.search_moment_recyclerview);

        SearchView searchView = findViewById(R.id.search_moment_searchview);
        searchView.setQuery(query, false);

        noDataTextView.setVisibility(View.GONE);
        //rogressBar.setVisibility(View.VISIBLE);



        List<String> searchWords=new ArrayList<>();
        searchWords.add(query);

        Toast.makeText(getApplicationContext(),"searchWords="+searchWords.get(0),Toast.LENGTH_SHORT).show();

        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        adapter=new MomentPagingAdapter(SearchMomentActivity.this);

        recyclerView.setAdapter(adapter);

        viewModel.getPaging(searchWords).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


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