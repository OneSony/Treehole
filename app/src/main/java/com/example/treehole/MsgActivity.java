package com.example.treehole;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MsgActivity extends AppCompatActivity {

    private TextInputLayout textInput;
    private Button sendButton;

    private int message_index;

    private RecyclerView recyclerView;
    private MsgListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

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

        recyclerView=findViewById(R.id.msg_list);
        adapter=new MsgListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Bundle bundle=getIntent().getBundleExtra("BUNDLE_DATA");
        if (bundle != null) {
            message_index = (int) bundle.getSerializable("DATA");
        }
        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        LiveData<Message> message= null;
        try {
            message = viewModel.getMessageByIndex(message_index);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //bar.setTitle(Objects.requireNonNull(message.getValue()).getUser());

        message.observe(this, message1 -> {
            if (message1 != null) {
                List<MessageNode> messageNodes=message1.getNodes();
                bar.setTitle(message1.getUser());
                adapter.setMessageNodes(messageNodes);
                adapter.notifyDataSetChanged();
            }
        });






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