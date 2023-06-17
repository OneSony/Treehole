package com.example.treehole.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.treehole.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.main_toolbar));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.Nav);
        NavController navController = navHostFragment.getNavController();
        bottomNavigationView = findViewById(R.id.BottomNav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(itemView.getId());
        badgeDrawable.setVisible(false);
        //badgeDrawable.setNumber(10); // 设置气泡的数字或文本内容

/*
        final BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        final View mTab = menuView.getChildAt(1);
        final BottomNavigationItemView itemView = (BottomNavigationItemView) mTab;
        View mBadge = itemView.getChildAt(3);//?
        if (mBadge == null) {
            //避免重复添加创建
            mBadge = LayoutInflater.from(bottomNavigationView.getContext()).inflate(R.layout.nav_dot, menuView, false);
            itemView.addView(mBadge);
        }
*/

    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable the back button
    }

    /*public void edit_click(View view) {

        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);

    }*/

}