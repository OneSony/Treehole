package com.example.treehole.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.paging.MomentPagingAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MomentPagingAdapter adapter;

    private TextView noDataTextView;

    private int searchType=0;//0是已发表，1是收藏

    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        searchType=getIntent().getIntExtra("SEARCH_TYPE",0);




        noDataTextView=findViewById(R.id.favourite_no_data);
        noDataTextView.setVisibility(View.GONE);

        progressBar=findViewById(R.id.favourite_progress);


        setSupportActionBar(findViewById(R.id.favourite_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("收藏");


        recyclerView=findViewById(R.id.favourite_recyclerview);



        final List<String>[] searchWords = new List[]{new ArrayList<>()};

        String query="test";
        String[] words= query.split(" ");
        for(String word:words){
            searchWords[0].add(word);
        }

        Toast.makeText(getApplicationContext(),"searchWords="+ searchWords[0].get(0),Toast.LENGTH_SHORT).show();

        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        adapter=new MomentPagingAdapter(FavouriteActivity.this);

        /*adapter.addLoadStateListener(loadStates -> {
            progressBar.setVisibility(loadStates.getRefresh() instanceof LoadState.Loading
                    ? View.VISIBLE : View.GONE);
            return null;
        });*/

        adapter.addLoadStateListener(loadStates-> {
            if (loadStates.getRefresh() instanceof LoadState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                // 数据源正在加载中
                // 可以显示加载中的动画或提示信息
            } else if (loadStates.getRefresh() instanceof LoadState.Error) {
                progressBar.setVisibility(View.GONE);
                // 数据源加载时遇到错误
                // 可以显示错误提示信息
            } else if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                progressBar.setVisibility(View.GONE);

                Log.d("PAGING","count"+adapter.getItemCount()+loadStates.getRefresh().getEndOfPaginationReached());
                if(adapter.getItemCount()==0) {
                    noDataTextView.setVisibility(View.VISIBLE);
                }

            }
            return null;
        });

        recyclerView.setAdapter(adapter);

        viewModel.getPaging("username", searchWords[0]).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        TabLayout tabLayout=findViewById(R.id.favourite_tablayout);
        TabLayout.Tab tab= tabLayout.getTabAt(searchType);
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

                // 根据特定标签执行相应逻辑
                searchType=tabPosition;

                Log.d("Search Type", "tabPosition=??"+tabPosition);


                adapter=new MomentPagingAdapter(FavouriteActivity.this);

                adapter.addLoadStateListener(loadStates-> {
                    if (loadStates.getRefresh() instanceof LoadState.Loading) {
                        progressBar.setVisibility(View.VISIBLE);
                        // 数据源正在加载中
                        // 可以显示加载中的动画或提示信息
                    } else if (loadStates.getRefresh() instanceof LoadState.Error) {
                        progressBar.setVisibility(View.GONE);
                        // 数据源加载时遇到错误
                        // 可以显示错误提示信息
                    } else if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                        progressBar.setVisibility(View.GONE);

                        Log.d("PAGING","count"+adapter.getItemCount()+loadStates.getRefresh().getEndOfPaginationReached());
                        if(adapter.getItemCount()==0) {
                            noDataTextView.setVisibility(View.VISIBLE);
                        }

                    }
                    return null;
                });

                recyclerView.setAdapter(adapter);

                viewModel.getPaging("username", searchWords[0]).observe(FavouriteActivity.this,
                        dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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