package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.paging.MomentPagingAdapter;

import java.util.List;


public class MainFragment_sub1 extends Fragment {

    private dot_list data_list;
    private RecyclerView recyclerView;
    //private dot_list_adapter adapter;
    private MomentPagingAdapter adapter;
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

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        recyclerView=view.findViewById(R.id.recycle_box);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter=new MomentPagingAdapter();
        recyclerView.setAdapter(adapter);
        MainViewModel loadMoreViewModel=new ViewModelProvider(this).get(MainViewModel.class);
        loadMoreViewModel.getPaging().observe(getViewLifecycleOwner(),
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新



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