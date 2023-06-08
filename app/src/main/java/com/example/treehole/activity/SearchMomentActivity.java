package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.SearchUserListAdapter;
import com.example.treehole.SearchUserResult;
import com.example.treehole.SearchViewModel;
import com.example.treehole.WebUtils;
import com.example.treehole.paging.MomentPagingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchMomentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserListAdapter adapterUser;
    private MomentPagingAdapter adapterMoment;

    private TextView noDataTextView;

    private int searchType;//0 user, 1 moment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);


        noDataTextView=findViewById(R.id.search_no_data);
        noDataTextView.setVisibility(View.GONE);

        ProgressBar progressBar=findViewById(R.id.search_progress);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(findViewById(R.id.search_user_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        String query=getIntent().getStringExtra("QUERY");
        searchType=getIntent().getIntExtra("SEARCH_TYPE",0);

        Toast.makeText(getApplicationContext(),"searchType="+String.valueOf(searchType),Toast.LENGTH_SHORT).show();


        recyclerView=findViewById(R.id.search_user_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        SearchView searchView = findViewById(R.id.search_user_searchview);
        searchView.setQuery(query, false);

        noDataTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if(searchType==0) {

            ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

            adapterUser = new SearchUserListAdapter(getApplicationContext());
            adapterUser.setOnItemClickListener(new SearchUserListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String user_id, String username) throws ExecutionException, InterruptedException {
                    //Intent intent = new Intent(getApplicationContext(), PersonActivity.class);

                    Intent intent = new Intent(getApplicationContext(), MsgActivity.class);
                    Bundle bundle = new Bundle();


                    bundle.putSerializable("DATA", viewModel.searchMessage(user_id, username));
                    intent.putExtra("BUNDLE_DATA", bundle);

                    startActivity(intent);
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // 处理搜索提交事件
                    noDataTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    adapterUser.setSearchUserResults(new ArrayList<>());
                    adapterUser.notifyDataSetChanged();

                    List<SearchUserResult> searchUserResults=new ArrayList<>();
                    searchAndInsert(query, searchUserResults, adapterUser,searchType);


                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // 处理搜索框文本变化事件
                    // 这里可以根据 newText 进行实时搜索或过滤操作
                    return true;
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            List<SearchUserResult> searchUserResults=new ArrayList<>();
            searchAndInsert(query, searchUserResults, adapterUser,searchType);

        }else if(searchType==1) {//moment search

            List<String> searchWords=new ArrayList<>();
            searchWords.add(query);

            Toast.makeText(getApplicationContext(),"searchWords="+searchWords.get(0),Toast.LENGTH_SHORT).show();

            SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

            adapterMoment=new MomentPagingAdapter(getApplicationContext());

            recyclerView.setAdapter(adapterMoment);

            viewModel.getPaging(searchWords).observe(this,
                    dataInfoPagingData -> adapterMoment.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }

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

    private void searchAndInsert(String query, List<SearchUserResult> listContainer, SearchUserListAdapter adapter,int searchType) {


        if (searchType == 0) {
            WebUtils.sendGet("/users/find_users?username=" + query, false, new WebUtils.WebCallback() {

                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        JSONArray users = json.getJSONArray("message");
                        if (users.length() == 0) {
                            Log.d("ADD", "NODATA");

                            runOnUiThread(new Runnable() {
                                public void run() {

                                    ProgressBar progressBar = findViewById(R.id.search_progress);
                                    progressBar.setVisibility(View.GONE);

                                    noDataTextView.setVisibility(View.VISIBLE);
                                }
                            });

                        } else {
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject user = users.getJSONObject(i);
                                String user_id = user.getString("user_id");
                                String username = user.getString("username");
                                listContainer.add(new SearchUserResult(user_id, username));
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {

                                    ProgressBar progressBar = findViewById(R.id.search_progress);
                                    progressBar.setVisibility(View.GONE);
                                    adapter.setSearchUserResults(listContainer);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        Log.e("SEARCHUSERACTIVITY", "ERROR: " + e.getMessage());
                    }

                }

                @Override
                public void onError(Throwable t) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),"搜索失败", Toast.LENGTH_SHORT).show();
                            ProgressBar progressBar = findViewById(R.id.search_progress);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    Log.e("SEARCHUSERACTIVITY", "ERROR: " + t.getMessage());
                }

                @Override
                public void onFailure(JSONObject json) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),"搜索失败", Toast.LENGTH_SHORT).show();
                            ProgressBar progressBar = findViewById(R.id.search_progress);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Log.e("SEARCHUSERACTIVITY", "FAILURE: " + json.optString("message", "onFailure"));
                }
            });


        }else if(searchType==1){





        }
    }
}