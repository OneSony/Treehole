package com.example.treehole.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.treehole.activity.FollowerActivity;
import com.example.treehole.activity.LoginActivity;
import com.example.treehole.R;
import com.example.treehole.activity.SettingsActivity;
import com.example.treehole.WebUtils;


public class MineFragment extends Fragment {


    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);


        Button exit_button = view.findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 响应事件
                Toast.makeText(getContext(),"退出成功",Toast.LENGTH_SHORT).show();

                /*String sharedPrefFile ="com.example.android.Treehole";
                SharedPreferences mPreferences = getActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putBoolean("LOGIN_SIT", false);
                preferencesEditor.apply();*/

                WebUtils.setLogIn(false);

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button test_button = view.findViewById(R.id.test_btn);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 响应事件
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mine_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 处理菜单项的点击事件
        if(item.getItemId() == R.id.action_set){

            //MainViewModel mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);
            //mainViewModel.insert(new Moment("TOPIC_NEW","TEXT"));

            Intent intent = new Intent(getActivity(), SettingsActivity.class);
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