package com.example.treehole.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.UserUtils;
import com.example.treehole.WebUtils;
import com.example.treehole.activity.FollowerActivity;
import com.example.treehole.activity.LoginActivity;
import com.example.treehole.activity.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MineFragment extends Fragment {

    String user_id;
    String username;

    TextView username_text;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!isAdded()) {
                return; // 页面已经不存在，不执行更新UI的操作
            }

            // 处理消息并更新UI
            switch (msg.what) {
                case 10:
                    if(username_text!=null&&msg.obj!=null){
                        username_text.setText((String)msg.obj);
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // 移除未处理的消息和处理程序
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

        user_id= UserUtils.getUserid();
        username=UserUtils.getUsername();

        username_text = view.findViewById(R.id.mine_my_username);
        username_text.setText(username);

        user_id= UserUtils.getUserid();
        if (user_id != "") {
            ImageView user_image = view.findViewById(R.id.mine_my_photo);
            String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+user_id;
            Glide.with(getContext()).load(profile_photo_url).into(user_image);
        }

        JSONObject json = new JSONObject();
        try {
            json.put("id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("URL","/users/username?id="+user_id);


        WebUtils.sendGet("/users/username?id="+user_id, false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject username_msg=json.getJSONObject("message");
                    username=username_msg.getString("username");
                    Log.d("SUCCESS", username);

                    WebUtils.setUsername(username);

                    Message msg=new Message();
                    msg.what=10;
                    msg.obj=username;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


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

        TextView my_username = view.findViewById(R.id.mine_my_username);


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