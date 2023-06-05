package com.example.treehole.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.ChatListAdapter;
import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.activity.MsgActivity;
import com.example.treehole.activity.SearchUserActivity;
import com.example.treehole.room.Message;
import com.example.treehole.room.MessageNode;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    private Menu menu;
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



        recyclerView=view.findViewById(R.id.chat_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        adapter=new ChatListAdapter();
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                launch_msg(index);
                return;
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        List<MessageNode> nodes=new ArrayList<>();
        nodes.add(new MessageNode(1,"msg1 from user1"));
        nodes.add(new MessageNode(1,"msg2 from user1"));
        nodes.add(new MessageNode(0,"msg3 from user2"));
        nodes.add(new MessageNode(1,"msg4 from user1"));

        //viewModel.insert(new Message("USERID1","USER1",nodes));



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

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 取消菜单项的选中状态
        MenuItem menuItem = menu.findItem(R.id.action_add_chat);
        menuItem.setChecked(false);
        menuItem.collapseActionView();

        // 关闭搜索框
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.clearFocus();
    }
}
