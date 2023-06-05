package com.example.treehole.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.treehole.R;
import com.example.treehole.fragment.MainFragment_sub2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class FollowerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private TabLayout tabLayout;
    ArrayList<Fragment> fragmentContainer = new ArrayList<Fragment>();
    ArrayList<String> titleList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        setSupportActionBar(findViewById(R.id.follower_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Follower");


        titleList.add("收藏");
        titleList.add("已发表");
        titleList.add("黑名单");
        fragmentContainer.add(new MainFragment_sub2());//新回复
        fragmentContainer.add(new MainFragment_sub2());//热门
        fragmentContainer.add(new MainFragment_sub2());//关注

        tabLayout = findViewById(R.id.follower_tab);
        viewPager = findViewById(R.id.follower_pager);

        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> tab.setText(titleList.get(position))).attach();

    }



    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FollowerActivity fa) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

