package com.example.treehole;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity {

    private TextInputLayout textInput;
    private Button sendButton;
    private dot curr_data;

    private RecyclerView recyclerView;
    private MsgListAdapter adapter;

    private dot_list data_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textInput = (TextInputLayout) findViewById(R.id.textInputLayout);
        sendButton = (Button) findViewById(R.id.message_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = textInput.getEditText();
                String messageText = editText.getText().toString();
                String receiverUsername = "";

                JSONObject json = new JSONObject();
                try{
                    json.put("receiver", receiverUsername);
                    json.put("type", "string");
                    json.put("message", messageText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebUtils.sendPost("/messaging/send/", true, json, new WebUtils.WebCallback() {
                    @Override
                    public void onSuccess(JSONObject json) {
                        Log.d("Message", "Message sent");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("Message", "Message send error: "+t.getMessage());
                    }

                    @Override
                    public void onFailure(JSONObject json) {
                        try {
                            String message = json.getString("message");
                            Log.e("Message", "Message send failed: "+message);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        });

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