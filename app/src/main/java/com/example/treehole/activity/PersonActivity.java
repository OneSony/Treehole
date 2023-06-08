package com.example.treehole.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.SearchViewModel;
import com.example.treehole.paging.MomentPagingAdapter;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {

    private String username;
    private String user_id;

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


        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        MomentPagingAdapter adapter=new MomentPagingAdapter(PersonActivity.this,false);

        RecyclerView recyclerView=findViewById(R.id.person_recyclerview);
        recyclerView.setAdapter(adapter);

        List<String> searchWords = new ArrayList<>();
        searchWords.add(username);

        viewModel.getPaging("username", searchWords).observe(this,
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
}