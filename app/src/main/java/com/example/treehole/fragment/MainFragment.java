package com.example.treehole.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.treehole.activity.EditActivity;
import com.example.treehole.R;
import com.example.treehole.application;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private TabLayout tabLayout;


    ArrayList<Fragment> fragmentContainer = new ArrayList<Fragment>();
    ArrayList<String> titleList = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        //添加子页面
        titleList.add("新发表");
        titleList.add("新回复");
        titleList.add("热门");
        titleList.add("关注");
        fragmentContainer.add(new MainFragment_sub1());//新发表
        fragmentContainer.add(new MainFragment_sub2());//新回复
        fragmentContainer.add(new MainFragment_sub2());//热门
        fragmentContainer.add(new MainFragment_sub2());//关注

    }

    @Override
    public void onResume() {
        super.onResume();
        //update_data_live();
    }

    public void update_data_live(){//可以换成LiveData？

        Fragment activeFragment = getChildFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());

        if (activeFragment != null && activeFragment instanceof MainFragment_sub1) {
            ((MainFragment_sub1) activeFragment).update_data_live();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        application app=(application)getActivity().getApplication();
        View view=inflater.inflate(R.layout.fragment_main, container, false);

        tabLayout = view.findViewById(R.id.main_tab);
        viewPager = view.findViewById(R.id.main_paper);

        // 先强制设置到指定页面

        // 通过数据修改
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {//切换回来的时候更新一下
                ((application) getActivity().getApplication()).main_frag_pager_id=position;
                if(position==0){
                    update_data_live();
                }
            }
        });
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();


        // 切换到指定页面
        viewPager.setCurrentItem(app.main_frag_pager_id);


        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> tab.setText(titleList.get(position))).attach();

        return view;
    }

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast toast=Toast.makeText(getActivity(),"MainFragment销毁了！",Toast.LENGTH_SHORT);
        //toast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("输入关键词");
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交事件
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
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

        /*if(item.getItemId() == R.id.action_refresh){
            update_data_live();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}