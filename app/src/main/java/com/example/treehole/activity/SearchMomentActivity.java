package com.example.treehole.activity;

import android.content.SharedPreferences;
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
import androidx.paging.LoadState;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.paging.MomentPagingAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchMomentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MomentPagingAdapter adapter;

    private TextView noDataTextView;

    private String searchType="";

    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_moment);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        searchType = sharedPreferences.getString("moment_search_type", "text");
        Log.d("Search Type", searchType);





        noDataTextView=findViewById(R.id.search_no_data_moment);
        noDataTextView.setVisibility(View.GONE);

        progressBar=findViewById(R.id.search_progress_moment);


        setSupportActionBar(findViewById(R.id.search_moment_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("搜索动态");

        String query = getIntent().getStringExtra("QUERY");


        recyclerView=findViewById(R.id.search_moment_recyclerview);



        final List<String>[] searchWords = new List[]{new ArrayList<>()};

        String[] words= query.split(" ");
        for(String word:words){
            searchWords[0].add(word);
        }

        //Toast.makeText(getApplicationContext(),"searchWords="+ searchWords[0].get(0),Toast.LENGTH_SHORT).show();

        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        adapter=new MomentPagingAdapter(SearchMomentActivity.this);

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

        viewModel.getPaging(searchType, searchWords[0]).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        SearchView searchView = findViewById(R.id.search_moment_searchview);
        searchView.setQuery(query, false);
        searchView.setFocusable(false);


        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noDataTextView.setVisibility(View.GONE);

                searchWords[0] =new ArrayList<>();
                String[] words=query.split(" ");
                for(String word:words){
                    searchWords[0].add(word);
                }

                // 处理搜索提交事件
                adapter=new MomentPagingAdapter(SearchMomentActivity.this);

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
                /*
                if (!loadStates.getRefresh().getEndOfPaginationReached()) {
                    // 加载完成且有更多数据可加载
                    // 可以根据数据列表是否为空来判断是否获得了数据
                    if (adapter.getItemCount() == 0) {
                        // 没有获得任何数据
                        // 可以显示空数据提示信息
                    }
                } else {
                    // 加载完成且没有更多数据可加载
                    // 可以根据数据列表是否为空来判断是否获得了数据
                    if (adapter.getItemCount() == 0) {
                        // 没有获得任何数据
                        // 可以显示空数据提示信息
                        Log.d("PAGING","Nothing!");
                    }
                }*/
                    }
                    return null;
                });

                recyclerView.setAdapter(adapter);

                viewModel.getPaging(searchType, searchWords[0]).observe(SearchMomentActivity.this,
                        dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 处理搜索框文本变化事件
                // 这里可以根据 newText 进行实时搜索或过滤操作
                return true;
            }
        });



        TabLayout tabLayout=findViewById(R.id.search_moment_tablayout);
        TabLayout.Tab tab;
        if(searchType.equals("text")) {
            tab = tabLayout.getTabAt(0);
        }else if(searchType.equals("username")) {
            tab = tabLayout.getTabAt(3);
        }else if(searchType.equals("location")) {
            tab = tabLayout.getTabAt(2);
        }else if(searchType.equals("tags")) {
            tab = tabLayout.getTabAt(1);
        }else{
            tab = tabLayout.getTabAt(0);
        }

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
                if (tabPosition==3) {
                    searchType = "username";
                } else if (tabPosition==0) {
                    searchType = "text";
                } else if (tabPosition==2) {
                    searchType = "location";
                } else if (tabPosition==1) {
                    searchType = "tags";
                }else {
                    return;
                }

                Log.d("Search Type", "tabPosition=??"+tabPosition);


                adapter=new MomentPagingAdapter(SearchMomentActivity.this);

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

                viewModel.getPaging(searchType, searchWords[0]).observe(SearchMomentActivity.this,
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