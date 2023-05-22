package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MainFragment_sub1 extends Fragment {

    private dot_list data_list;
    private RecyclerView recyclerView;
    private dot_list_adapter adapter;
    private application app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main_sub1, container, false);

        app=(application)getActivity().getApplication();
        data_list=app.data_list;

        recyclerView=view.findViewById(R.id.recycle_box);
        adapter=new dot_list_adapter(getActivity(),data_list);
        adapter.setOnItemClickListener(new dot_list_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launch_info(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Toast toast=Toast.makeText(getActivity(),"MainFragment_sub1绘画",Toast.LENGTH_SHORT);
        //toast.show();
        if(recyclerView.getLayoutManager() != null && app.lastPosition >= 0) {
            /*if(app.button_flag == true){

            } else */{
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(app.lastPosition, app.lastOffset);
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                app.button_flag = false;
                if(recyclerView.getLayoutManager() != null) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    //获取可视的第一个view
                    View topView = layoutManager.getChildAt(0);
                    if(topView != null) {
                        //获取与该view的顶部的偏移量
                        app.lastOffset = topView.getTop();
                        //得到该View的数组位置
                        app.lastPosition = layoutManager.getPosition(topView);
                    }
                }
            }
        });
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        Log.d("CHAT",String.valueOf(data_list.size()));
        return view;
    }

    private void launch_info(int index) {
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        Bundle bundle=new Bundle();
        //bundle.putString("title","Activity 2");
        bundle.putSerializable("DATA",data_list.get(index));

        intent.putExtra("BUNDLE_DATA",bundle);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.Nav);
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Fragment currentFragment = null;
        for (Fragment fragment : fragmentList) {
            if (fragment.isVisible()) {
                currentFragment = fragment;
                break;
            }
        }

        app=(application)getActivity().getApplication();
        if (currentFragment instanceof MainFragment) {
            //((MainFragment) currentFragment).update_data_live();
            Log.d("curID",String.valueOf(currentFragment.getId()));
            Log.d("home",String.valueOf(R.id.mainFragment));
            app.main_navigation_id=R.id.mainFragment;
        }else if(currentFragment instanceof ChatFragment){
            app.main_navigation_id=R.id.chatFragment;
        }else if(currentFragment instanceof MineFragment){
            app.main_navigation_id=R.id.mineFragment;
        }


        //String topic_msg=data_list.get(index).getTopic().toString();
        //String main_msg=data_list.get(index).getText().toString();
        //intent.putExtra("TOPIC",topic_msg);
        //intent.putExtra("MAIN",main_msg);
        /*NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.Nav);
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Fragment currentFragment = null;
        for (Fragment fragment : fragmentList) {
            if (fragment.isVisible()) {
                currentFragment = fragment;
                break;
            }
        }
        if(currentFragment!=null){
            app=(application)getActivity().getApplication();
            app.main_navigation_id=currentFragment.getId();
        }*/


        startActivity(intent);
    }

    public void update_data_live(){
        app=(application)getActivity().getApplication();
        data_list=app.data_list;
        adapter.notifyDataSetChanged();
        Log.d("UPDATE","UPDATA!");
    }
}