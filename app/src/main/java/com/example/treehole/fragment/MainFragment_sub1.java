package com.example.treehole.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.treehole.MainViewModel;
import com.example.treehole.R;
import com.example.treehole.application;
import com.example.treehole.dot_list;
import com.example.treehole.paging.MomentPagingAdapter;


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


        TextView no_data=view.findViewById(R.id.main_no_data);
        no_data.setVisibility(View.GONE);


        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_data.setVisibility(View.GONE);
                update_data_live();
                //swipeRefreshLayout.setRefreshing(false);
                Log.d("REFRESH","YEAH!");
            }
        });



        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //viewModel.deleteAll();
/*
        for(int i=0;i<2;i++){
            viewModel.insert(new Moment("TOPIC "+String.valueOf(i),"TEXT "+String.valueOf(i)));
        }

 */

/*
        try {
            Log.d("SIZE",String.valueOf(viewModel.getMomentCount()));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/
        recyclerView=view.findViewById(R.id.recycle_box);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter=new MomentPagingAdapter(getActivity());

        adapter.addLoadStateListener(loadStates-> {
                if (loadStates.getRefresh() instanceof LoadState.Loading) {
                    swipeRefreshLayout.setRefreshing(true);
                    // 数据源正在加载中
                    // 可以显示加载中的动画或提示信息
                } else if (loadStates.getRefresh() instanceof LoadState.Error) {
                    swipeRefreshLayout.setRefreshing(false);
                    // 数据源加载时遇到错误
                    // 可以显示错误提示信息
                } else if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                    swipeRefreshLayout.setRefreshing(false);
                    if(adapter.getItemCount()==0){
                        no_data.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            });


        recyclerView.setAdapter(adapter);
        MainViewModel loadMoreViewModel=new ViewModelProvider(this).get(MainViewModel.class);


        loadMoreViewModel.getPaging().observe(getViewLifecycleOwner(),
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新



        return view;
    }
/*
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
*/
/*
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
*/

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

/*
        startActivity(intent);
    }
*/
    public void update_data_live(){
        //app=(application)getActivity().getApplication();
        //data_list=app.data_list;
        //adapter.notifyDataSetChanged();
        adapter.refresh();

        recyclerView.scrollToPosition(0);
        Log.d("UPDATE","UPDATA!");
    }
}