package com.example.treehole.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.treehole.MainViewModel;
import com.example.treehole.R;
import com.example.treehole.activity.EditActivity;
import com.example.treehole.activity.SearchMomentActivity;
import com.example.treehole.paging.MomentPagingAdapter;

public class MainFragment extends Fragment {

    private Menu menu;
    private RecyclerView recyclerView;
    //private dot_list_adapter adapter;
    private MomentPagingAdapter adapter;

    private MainViewModel viewModel;

    private String sortType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = sharedPreferences.getString("main_sort_type", "date");
        Log.d("sort Type", sortType);


        viewModel=new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.setDefault_sortType(sortType);
}

    @Override
    public void onResume() {
        super.onResume();

        // 取消菜单项的选中状态

        if(menu!=null) {
            MenuItem menuItem = menu.findItem(R.id.action_search);
            menuItem.setChecked(false);
            menuItem.collapseActionView();


            // 关闭搜索框
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.clearFocus();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //application app=(application)getActivity().getApplication();
        View view=inflater.inflate(R.layout.fragment_main, container, false);

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


        recyclerView=view.findViewById(R.id.recycle_box);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter=new MomentPagingAdapter(getActivity());

        adapter.addLoadStateListener(loadStates-> {
            if (loadStates.getRefresh() instanceof LoadState.Loading) {
                swipeRefreshLayout.setRefreshing(true);
                no_data.setVisibility(View.GONE);
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

        viewModel.getPaging().observe(getViewLifecycleOwner(),
                dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新



        return view;
    }
/*
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(MainFragment fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragmentContainer.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentContainer.size();
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast toast=Toast.makeText(getActivity(),"MainFragment销毁了！",Toast.LENGTH_SHORT);
        //toast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu=menu;
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("输入关键词");
        searchView.setIconifiedByDefault(true);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件
                //Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), SearchMomentActivity.class);
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


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 处理菜单项的点击事件
        if(item.getItemId() == R.id.action_add){

            //MainViewModel mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);
            //mainViewModel.insert(new Moment("TOPIC_NEW","TEXT"));

            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);

            return true;
        }


        if(item.getItemId() == R.id.action_sort){
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void update_data_live(){
        adapter.refresh();
        recyclerView.scrollToPosition(0);
        Log.d("UPDATE","UPDATA!");
    }



    // 在你的 Activity 或 Fragment 中创建对话框
    private void showSortDialog() {

        // 默认选中的排序方式
        int defaultSortOption = 0;

        if (sortType.equals("date")) {
            defaultSortOption = 0;
        } else if (sortType.equals("likes")) {
            defaultSortOption = 1;
        } else if(sortType.equals("follow")) {
            defaultSortOption = 2;
        }

        // 选项列表
        final String[] sortOptions = {"时间排序", "热度排序","只看关注"};

        // 构建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择排序方式")
                .setSingleChoiceItems(sortOptions, defaultSortOption, null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取选中的选项
                        int selectedOption = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        // 提交选中的选项
                        submitSortOption(selectedOption);
                    }
                })
                .setNegativeButton("取消", null);

        // 显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 提交选中的选项
    private void submitSortOption(int selectedOption) {
        // 根据选项执行相应的操作
        switch (selectedOption) {
            case 0:
                viewModel.getNewPaging("date").observe(getViewLifecycleOwner(),
                        dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新
                //update_data_live();
                sortType="date";
                break;
            case 1:
                viewModel.getNewPaging("likes").observe(getViewLifecycleOwner(),
                        dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新
                //update_data_live();
                sortType="likes";
                break;
            case 2:
                viewModel.getNewPaging("date","follow").observe(getViewLifecycleOwner(),
                        dataInfoPagingData -> adapter.submitData(getLifecycle(),dataInfoPagingData));//观察数据的更新
                //update_data_live();
                sortType="follow";
                break;
            default:
                break;
        }
    }


}