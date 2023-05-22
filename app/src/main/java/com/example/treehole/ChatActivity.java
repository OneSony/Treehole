package com.example.treehole;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    private TextView textView;
    private dot curr_data;

    private RecyclerView recyclerView;
    private MsgListAdapter adapter;

    private dot_list data_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setSupportActionBar(findViewById(R.id.chat_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("私信");

        data_list=new dot_list();

        Bundle bundle=getIntent().getBundleExtra("BUNDLE_DATA");
        if (bundle != null) {

            curr_data = (dot) bundle.getSerializable("DATA");

            Log.d("CHAT in",curr_data.getTopic());
            Log.d("CHAT in",curr_data.getText());


            data_list.insert(curr_data.getTopic(),curr_data.getText(),curr_data.getAuth(),curr_data.getProfile_index());
            data_list.insert(curr_data.getTopic(),curr_data.getText(),curr_data.getAuth(),curr_data.getProfile_index());

            Log.d("CHAT in",String.valueOf(data_list.size()));
        }

        bar.setTitle(curr_data.getAuth());

        recyclerView=findViewById(R.id.msg_list);
        adapter=new MsgListAdapter(this,data_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



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