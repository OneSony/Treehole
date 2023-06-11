package com.example.treehole.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.treehole.ChatListAdapter;
import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.UserUtils;
import com.example.treehole.WebUtils;
import com.example.treehole.activity.MsgActivity;
import com.example.treehole.activity.SearchUserActivity;
import com.example.treehole.application;
import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;
import com.example.treehole.room.MessageQueueNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {


    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    private TextView noDataTextView;

    private Menu menu;

    private ChatViewModel viewModel;
    //private ChatViewModel viewModel;
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        application myApplication = (application) requireActivity().getApplication();


        Observer<ArrayList<MessageQueueNode>> unreadMessagesObserver = messageQueue -> {

            // 更新未读消息列表

            ArrayList<MessageQueueNode> unreadMessages = new ArrayList<>();
            unreadMessages.addAll(messageQueue);
            messageQueue.clear();

            Log.d("GOT IN chat",String.valueOf(messageQueue.size()));

            // 处理未读消息
            for (MessageQueueNode messageQueueNode : unreadMessages) {
                viewModel.receiveMessageNode(messageQueueNode.getSenderId(), messageQueueNode.getSenderUsername(), messageQueueNode.getMessageNode());
            }

            unreadMessages.clear();
        };

        myApplication.getUnreadMessagesLiveData().observe(getViewLifecycleOwner(), unreadMessagesObserver);




        //IntentFilter filter = new IntentFilter("com.example.treehole.NEW_MESSAGE_RECEIVED");
        //LocalBroadcastManager.getInstance(requireContext()).registerReceiver(messageReceiver, filter);


        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.chatSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LiveData<List<Message>> allMessage;
                allMessage = viewModel.getAllMessage();

                if(allMessage.getValue()==null){
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                for(Message message:allMessage.getValue()){

                    JSONObject json = new JSONObject();
                    try {
                        json.put("id", message.getUser_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("URL","/users/username?id="+message.getUser_id());
                    WebUtils.sendGet("/users/username?id="+message.getUser_id(), false, new WebUtils.WebCallback() {
                        @Override
                        public void onSuccess(JSONObject json) {

                            String username;
                            try {
                                JSONObject msg=json.getJSONObject("message");
                                username=msg.getString("username");
                                Log.d("SUCCESS", username);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            if(username!=null&&!(username.equals(message.getUser_id()))){
                                viewModel.updateUserInfo(message.getUser_id(), username);
                                Log.d("VIEWMODEL", "update user info");

                                //run in UI thread
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("ERROR", t.getMessage());
                        }

                        @Override
                        public void onFailure(JSONObject json) {
                            try {
                                Log.e("FAILURE", json.getString("message"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        Button button = view.findViewById(R.id.chat_temp_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.receiveMessageNode(UserUtils.getUserid(), "", new MessageNode(0, "hi"));
            }
        });

        noDataTextView=view.findViewById(R.id.chat_no_data);

        recyclerView=view.findViewById(R.id.chat_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        adapter=new ChatListAdapter();
        adapter.setAdapterCallback(new ChatListAdapter.AdapterCallback() {
            @Override
            public void onDataEmpty(boolean isEmpty) {
                if (isEmpty) {
                    recyclerView.setVisibility(View.GONE);
                    noDataTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataTextView.setVisibility(View.GONE);
                }
            }
        });
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                launch_msg(index);
                return;
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private final ColorDrawable background = new ColorDrawable(Color.RED);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 在这里处理向左滑动删除某一项的逻辑
                int position = viewHolder.getAdapterPosition();
                // 执行删除操作，例如：
                viewModel.deleteMessageByIndex(adapter.getIndex(position));
                adapter.deleteItem(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    int itemHeight = itemView.getHeight();

                    // 根据滑动距离 dX 的值绘制背景色
                    if (dX < 0) {
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        background.setBounds(0, 0, 0, 0);
                    }
                    background.draw(c);

                    // 绘制文本或其他视觉效果
                    // ...

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);







        List<MessageNode> nodes=new ArrayList<>();
        nodes.add(new MessageNode(1,"msg1 from user1"));
        nodes.add(new MessageNode(1,"msg2 from user1"));
        nodes.add(new MessageNode(0,"msg3 from user2"));
        nodes.add(new MessageNode(1,"msg4 from user1"));

        //viewModel.insert(new Message("USER1",nodes));



        LiveData<List<Message>> messages=viewModel.getAllMessage();
        messages.observe(getViewLifecycleOwner(), messages1 -> {
            adapter.setMessages(messages1);
            adapter.notifyDataSetChanged();
        });




        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu=menu;
        inflater.inflate(R.menu.chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_add_chat);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("输入用户名");
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                intent.putExtra("QUERY",query);
                intent.putExtra("SEARCH_TYPE",0);//user search
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
/*
        // 处理菜单项的点击事件
        if(item.getItemId() == R.id.action_chat_add){
            Intent intent = new Intent(getActivity(), CreateChatActivity.class);
            startActivity(intent);

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    private void launch_msg(int index) {
        Intent intent = new Intent(getActivity(), MsgActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("DATA",index);
        intent.putExtra("BUNDLE_DATA",bundle);
        intent.putExtra("FROM",0);

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 取消菜单项的选中状态

        if(menu!=null) {
            MenuItem menuItem = menu.findItem(R.id.action_add_chat);
            menuItem.setChecked(false);
            menuItem.collapseActionView();


            // 关闭搜索框
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.clearFocus();
        }
    }


}
