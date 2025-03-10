package com.example.treehole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.adapter.MsgListAdapter;
import com.example.treehole.R;
import com.example.treehole.utils.WebUtils;
import com.example.treehole.application;
import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;
import com.example.treehole.room.MessageQueueNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MsgActivity extends AppCompatActivity {

    private TextInputLayout textInput;
    private Button sendButton;

    private int message_index;

    private RecyclerView recyclerView;
    private MsgListAdapter adapter;

    private String username="";

    boolean isFirstLoad = true;

    private LiveData<Message> message= null;

    private int from_index;//从消息界面来，1从个人页面来
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);


        application myApplication = (application) getApplication();



        Observer<ArrayList<MessageQueueNode>> unreadMessagesObserver = messageQueue -> {

            ArrayList<MessageQueueNode> unreadMessages = new ArrayList<>();
            unreadMessages.addAll(messageQueue);
            messageQueue.clear();

            Log.d("GOT IN chat",String.valueOf(messageQueue.size()));

            // 处理未读消息
            for (MessageQueueNode messageQueueNode : unreadMessages) {
                if(message!=null) {
                    viewModel.receiveMessageNode(messageQueueNode.getSenderId(), messageQueueNode.getSenderUsername(), messageQueueNode.getMessageNode(), message.getValue().getIndex());
                }else{
                    viewModel.receiveMessageNode(messageQueueNode.getSenderId(), messageQueueNode.getSenderUsername(), messageQueueNode.getMessageNode());
                }
            }

            unreadMessages.clear();
        };

        myApplication.getUnreadMessagesLiveData().observe(this, unreadMessagesObserver);



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
            viewModel.cleanMessageUnread(message_index);
        }
        from_index = getIntent().getIntExtra("FROM",0);

        try {
            message = viewModel.getMessageByIndex(message_index);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //bar.setTitle(Objects.requireNonNull(message.getValue()).getUser());

        message.observe(this, message1 -> {
            if (message1 != null) {
                List<MessageNode> messageNodes=message1.getNodes();
                username=message1.getUsername();
                bar.setTitle(username);
                adapter.setMessageNodes(messageNodes);
                adapter.notifyDataSetChanged();


                int lastItemPosition = adapter.getItemCount() - 1;
                recyclerView.scrollToPosition(lastItemPosition);

/*
                if (isFirstLoad) { // 只在第一次加载数据时滑动到最下面
                    isFirstLoad = false; // 将标志设置为false，避免以后每次更新数据都滑动到最下面

                    // 滑动RecyclerView到最下面
                    int lastItemPosition = adapter.getItemCount() - 1;
                    recyclerView.scrollToPosition(lastItemPosition);
                    // 或者使用平滑滚动
                    // recyclerView.smoothScrollToPosition(lastItemPosition);
                }*/
            }
        });





        textInput = (TextInputLayout) findViewById(R.id.textInputLayout);
        EditText editText = textInput.getEditText();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {   // 按下完成按钮，这里和上面imeOptions对应

                    EditText editText = textInput.getEditText();
                    String messageText = editText.getText().toString();

                    if(messageText.equals("")){//不发送空消息
                        return false;
                    }

                    viewModel.addMessageNode(message_index, new MessageNode(1, messageText));

                    JSONObject json = new JSONObject();
                    try{
                        json.put("receiver", message.getValue().getUser_id());//message.getValue().getUsername());

                        json.put("type", "string");
                        json.put("message", messageText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("Message", "Message send: "+json.toString());
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

                    editText.setText("");//清空内容
                }
                return false;//返回true，保留软键盘。false，隐藏软键盘
            }
        });



        //-----------把发送消息移动到键盘中
/*
        sendButton = (Button) findViewById(R.id.message_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = textInput.getEditText();
                String messageText = editText.getText().toString();
                String receiverUsername = "";

                viewModel.addMessageNode(message_index, new MessageNode(1, messageText));
                editText.setText("");//清空内容


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


*/



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            // android.R.id.home 这个是获取ids.xml页面的返回箭头，项目自带的，要加上android

            case android.R.id.home:
                // 返回
                //this.finish();
                // 结束

                if(from_index==0) {
                    intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                }else{
                    finish();
                    return true;
                }

            case R.id.action_person_page:

                if(message!=null&&message.getValue()!=null) {
                    intent = new Intent(getApplicationContext(), PersonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("USERNAME", message.getValue().getUsername());
                    bundle.putString("USER_ID", message.getValue().getUser_id());
                    intent.putExtra("BUNDLE_DATA", bundle);
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.msg_menu, menu);
        return true;
    }
}