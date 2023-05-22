package com.example.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class MineFragment extends Fragment {


    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
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


        return view;
    }


}