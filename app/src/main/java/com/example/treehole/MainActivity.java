package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        /*FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.MainPageFragment, new MainFragment());
        transaction.commit();*/
        /*
        app=(application)getApplication();
        data_list=app.data_list;
        Log.d("Hello",String.valueOf(data_list.size()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recycle_box1);
        adapter=new dot_list_adapter(this,data_list,this);
        adapter.setOnItemClickListener(new dot_list_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launch_info(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(recyclerView.getLayoutManager() != null && app.lastPosition >= 0) {
            if(app.buttom_flag==true){

            } else {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(app.lastPosition, app.lastOffset);
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                app.buttom_flag=false;
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
        */
    }

    /*public void launch_info(int index) {
        Intent intent = new Intent(this, InfoActivity.class);
        Bundle bundle=new Bundle();
        //bundle.putString("title","Activity 2");
        bundle.putSerializable("DATA",data_list.get(index));

        intent.putExtra("BUNDLE_DATA",bundle);
        //String topic_msg=data_list.get(index).getTopic().toString();
        //String main_msg=data_list.get(index).getText().toString();
        //intent.putExtra("TOPIC",topic_msg);
        //intent.putExtra("MAIN",main_msg);

        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(LOG_TAG, "onStop");
    }

*/

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("123", "onDestroy");
    }
    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){

            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);

            //todo
            //application app=(application) getApplication();
            //app.data_list.insert("新主题！来自菜单！","新内容新内容新内容！\n但是没有照片","我",app.getID("user1"));

            /*
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.Nav);
            FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
            List<Fragment> fragmentList = fragmentManager.getFragments();
            Fragment currentFragment = null;
            for (Fragment fragment : fragmentList) {
                if (fragment.isVisible()) {
                    currentFragment = fragment;
                    break;
                }
            }
            if (currentFragment instanceof MainFragment) {
                Log.d("MAIN","UPDATA!!!");
                ((MainFragment) currentFragment).update_data_live();
            }*/

            /*app.button_flag =true;
            Toast toast=Toast.makeText(this,"已发布",Toast.LENGTH_SHORT);
            toast.show();*/

            //data_list.insert("新主题！来自菜单！","新内容新内容新内容！\n但是没有照片","我",app.getID("user1"));
            //Log.d("Hello2",String.valueOf(data_list.size()));
            //adapter.notifyDataSetChanged();
            //recyclerView.scrollToPosition(0);
            //app.buttom_flag=true;
            //Toast toast=Toast.makeText(this,"已发布",Toast.LENGTH_SHORT);
            //toast.show();

        //}
        //return super.onOptionsItemSelected(item);
    //}

    public void edit_click(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }
}