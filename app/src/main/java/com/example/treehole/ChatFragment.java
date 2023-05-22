package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private dot_list data_list;
    private application app;
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

        app=(application)getActivity().getApplication();
        data_list=app.data_list;

        recyclerView=view.findViewById(R.id.chat_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        adapter=new ChatListAdapter(getActivity(),data_list);
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launch_msg(position);
                return;
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.d("CHAT","?");


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void launch_msg(int index) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("DATA",data_list.get(index));

        intent.putExtra("BUNDLE_DATA",bundle);

        startActivity(intent);
    }
}

