package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //private dot_list data_list=new dot_list();
    private dot_list data_list;
    private RecyclerView recyclerView;
    private dot_list_adapter adapter;
    private BottomNavigationView bottomNavigationView;

    private application app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.main_toolbar));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.Nav);
        NavController navController = navHostFragment.getNavController();
        bottomNavigationView = findViewById(R.id.BottomNav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        app=(application)getApplication();
        navController.navigate(app.main_navigation_id);//先导航到默认主页

        //如何保证activity切换回来后继续保持原来的navigation
    }

    public void edit_click(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }
}