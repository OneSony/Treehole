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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.UserUtils;
import com.example.treehole.WebUtils;
import com.example.treehole.activity.FavouriteActivity;
import com.example.treehole.activity.FollowerActivity;
import com.example.treehole.activity.LoginActivity;
import com.example.treehole.activity.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MineFragment extends Fragment {

    String user_id;
    String username;

    TextView username_text;
    TextView follow_count;
    TextView follower_count;
    ImageView user_image;

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
                case 40:
                    if(follower_count !=null&&msg.obj!=null){
                        follower_count.setText(String.valueOf(msg.obj));
                    }
                    break;
                case 60:
                    if(follow_count !=null&&msg.obj!=null){
                        follow_count.setText(String.valueOf(msg.obj));
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
    public void onResume() {
        super.onResume();
        Log.d("RESUME", "RESUME");
        user_id= UserUtils.getUserid();
        if (user_id != "") {
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

                    UserUtils.setUsername(username);

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

        WebUtils.sendGet("/users/follow_count/", false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    Integer follow_count = responseJson.optInt("count", 0);
                    Log.d("SUCCESS", String.valueOf(follow_count));


                    Message msg=new Message();
                    msg.what=60;
                    msg.obj=follow_count;
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

        WebUtils.sendGet("/users/follower_count/", false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {

                try {
                    JSONObject responseJson=json.getJSONObject("message");
                    Integer follower_count = responseJson.optInt("count", 0);
                    Log.d("SUCCESS", String.valueOf(follower_count));


                    Message msg=new Message();
                    msg.what=40;
                    msg.obj=follower_count;
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

        follow_count = view.findViewById(R.id.textView6);
        follow_count.setText("-");

        follower_count = view.findViewById(R.id.textView4);
        follower_count.setText("-");

        user_image = view.findViewById(R.id.mine_my_photo);

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


        {
            LinearLayout follow_layout = view.findViewById(R.id.follow_layout);
            follow_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FollowerActivity.class);
                    intent.putExtra("SEARCH_TYPE", 0);
                    getActivity().startActivity(intent);
                }
            });
        }
        {
            LinearLayout fans_layout = view.findViewById(R.id.fans_layout);
            fans_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FollowerActivity.class);
                    intent.putExtra("SEARCH_TYPE", 1);
                    getActivity().startActivity(intent);
                }
            });
        }

        {
            ConstraintLayout blacklist_layout = view.findViewById(R.id.blacklist_layout);
            blacklist_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FollowerActivity.class);
                    intent.putExtra("SEARCH_TYPE", 2);
                    getActivity().startActivity(intent);
                }
            });
        }

        {
            ConstraintLayout favourite_layout = view.findViewById(R.id.favourite_layout);
            favourite_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FavouriteActivity.class);
                    intent.putExtra("SEARCH_TYPE", 1);
                    getActivity().startActivity(intent);
                }
            });
        }

        {
            ConstraintLayout published_layout = view.findViewById(R.id.published_layout);
            published_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FavouriteActivity.class);
                    intent.putExtra("SEARCH_TYPE", 0);
                    getActivity().startActivity(intent);
                }
            });
        }




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