package com.example.treehole.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.treehole.adapter.CommentListAdapter;
import com.example.treehole.R;
import com.example.treehole.utils.UserUtils;
import com.example.treehole.utils.WebUtils;
import com.example.treehole.room.Comment;
import com.example.treehole.room.Moment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.noties.markwon.Markwon;

public class InfoActivity extends AppCompatActivity {
    private TextView topic_box;
    private TextView main_box;

    private TextView auth_box;
    private TextView date_box;
    private ImageView profile_photo;

    private Moment current_moment;


    private ImageView[] imageViewArray;
    private ConstraintLayout[] constraintLayouts;
    private TextInputLayout commentInput;

    private LinearLayout photos;

    private PlayerView video;

    ExoPlayer player;

    private String user_id;

    private SwipeRefreshLayout swipeRefreshLayout;

    TextView no_data;

    private ImageView like_icon;
    private ImageView favourite_icon;

    private TextView like_box;
    private TextView favourite_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setSupportActionBar(findViewById(R.id.info_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle=getIntent().getBundleExtra("BUNDLE_DATA");
        if (bundle != null) {
            current_moment = (Moment) bundle.getSerializable("MOMENT");
        }


        profile_photo = findViewById(R.id.profile);
        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", current_moment.getUsername());
                bundle.putString("USER_ID", current_moment.getUser_id());
                intent.putExtra("BUNDLE_DATA", bundle);
                startActivity(intent);
            }
        });

        String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+current_moment.getUser_id();
        Glide.with(getApplicationContext()).load(profile_photo_url).into(profile_photo);

        ImageView my_profile_photo= findViewById(R.id.info_my_profile);
        user_id= UserUtils.getUserid();
        if(user_id!=""){
            Glide.with(getApplicationContext()).load("https://rickyvu.pythonanywhere.com/users/profile_picture?id="+user_id).into(my_profile_photo);
        }

        topic_box = findViewById(R.id.topic_box);
        main_box = findViewById(R.id.main_box);
        auth_box = findViewById(R.id.auth_box);
        date_box = findViewById(R.id.date_box);

        topic_box.setText(current_moment.getTopic());
        main_box.setText(current_moment.getText());
        auth_box.setText(current_moment.getUsername());
        date_box.setText(current_moment.getFullDate());



        imageViewArray = new ImageView[9];
        imageViewArray[0]=findViewById(R.id.info_photo1);
        imageViewArray[1]=findViewById(R.id.info_photo2);
        imageViewArray[2]=findViewById(R.id.info_photo3);
        imageViewArray[3]=findViewById(R.id.info_photo4);
        imageViewArray[4]=findViewById(R.id.info_photo5);
        imageViewArray[5]=findViewById(R.id.info_photo6);
        imageViewArray[6]=findViewById(R.id.info_photo7);
        imageViewArray[7]=findViewById(R.id.info_photo8);
        imageViewArray[8]=findViewById(R.id.info_photo9);

        constraintLayouts = new ConstraintLayout[9];
        constraintLayouts[0]=findViewById(R.id.info_photo_layout1);
        constraintLayouts[1]=findViewById(R.id.info_photo_layout2);
        constraintLayouts[2]=findViewById(R.id.info_photo_layout3);
        constraintLayouts[3]=findViewById(R.id.info_photo_layout4);
        constraintLayouts[4]=findViewById(R.id.info_photo_layout5);
        constraintLayouts[5]=findViewById(R.id.info_photo_layout6);
        constraintLayouts[6]=findViewById(R.id.info_photo_layout7);
        constraintLayouts[7]=findViewById(R.id.info_photo_layout8);
        constraintLayouts[8]=findViewById(R.id.info_photo_layout9);

        photos=findViewById(R.id.info_photos);
        video=findViewById(R.id.info_video);

        like_icon=findViewById(R.id.info_like_icon);
        favourite_icon=findViewById(R.id.info_collect_icon);

        like_box=findViewById(R.id.info_like_box);
        favourite_box=findViewById(R.id.info_collect_box);


        if(current_moment.getText_type().equals("markdown")){
            Markwon markwon = Markwon.create(getApplicationContext());
            //markdownTextView.setMovementMethod(new ScrollingMovementMethod()); // 启用滚动
            markwon.setMarkdown(main_box,current_moment.getText());
        }


        if(current_moment.isLiked()){
            like_icon.setImageResource(R.drawable.like_true);
        }else{
            like_icon.setImageResource(R.drawable.like_false);
        }

        if(current_moment.isFavourite()){
            favourite_icon.setImageResource(R.drawable.collect_true);
        }else{
            favourite_icon.setImageResource(R.drawable.collect_false);
        }

        like_box.setText(String.valueOf(current_moment.getLikes_num()));
        favourite_box.setText(String.valueOf(current_moment.getFavourite_num()));


        findViewById(R.id.info_like_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like_num=Integer.parseInt(like_box.getText().toString());
                if(current_moment.isLiked()){
                    like_num--;
                    current_moment.likes_num_minus();
                    current_moment.setLiked(false);
                    like_icon.setImageResource(R.drawable.like_false);
                }else{
                    like_num++;
                    current_moment.likes_num_add();
                    current_moment.setLiked(true);
                    like_icon.setImageResource(R.drawable.like_true);
                }
                like_box.setText(String.valueOf(like_num));


                JSONObject json = new JSONObject();
                try {
                    json.put("id", current_moment.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WebUtils.sendPost("/posts/like/", true, json, new WebUtils.WebCallback() {
                    @Override
                    public void onSuccess(JSONObject json) {
                        try {
                            Log.d("SUCCESS", json.getString("message"));
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
        });

        findViewById(R.id.info_collect_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int favourite_num=Integer.parseInt(favourite_box.getText().toString());
                if(current_moment.isFavourite()){
                    favourite_num--;
                    current_moment.favourite_num_minus();
                    current_moment.setFavourite(false);
                    favourite_icon.setImageResource(R.drawable.collect_false);
                }else{
                    favourite_num++;
                    current_moment.favourite_num_add();
                    current_moment.setFavourite(true);
                    favourite_icon.setImageResource(R.drawable.collect_true);
                }
                favourite_box.setText(String.valueOf(favourite_num));


                JSONObject json = new JSONObject();
                try {
                    json.put("id", current_moment.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WebUtils.sendPost("/posts/favourite/", true, json, new WebUtils.WebCallback() {
                    @Override
                    public void onSuccess(JSONObject json) {
                        try {
                            Log.d("SUCCESS", json.getString("message"));
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
        });







        int photo_num=current_moment.getImages().size();

        if(photo_num==0){
            photos.setVisibility(View.GONE);
        }else{
            photos.setVisibility(View.VISIBLE);

            for(int i=0;i<photo_num;i++){
                constraintLayouts[i].setVisibility(View.VISIBLE);
                imageViewArray[i].setVisibility(View.VISIBLE);
                int finalI = i;
                imageViewArray[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(InfoActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                        dialog.setContentView(R.layout.dialog_photo);

                        // 创建动画对象
                        Animation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(150); // 设置动画持续时间

// 将动画应用到对话框的窗口
                        Window window = dialog.getWindow();
                        if (window != null) {
                            window.setWindowAnimations(android.R.style.Animation_Dialog); // 设置窗口动画样式
                            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                PhotoView photoView=dialog.findViewById(R.id.dialog_photo);
                                Glide.with(getApplicationContext()).load(current_moment.getImages().get(finalI)).into(photoView);
                                photoView.setVisibility(View.VISIBLE);

                                Button button=dialog.findViewById(R.id.dialog_button);
                                if (button != null) {
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss(); // 点击对话框根布局，关闭对话框
                                        }
                                    });
                                }
                            }
                        });


                        // 显示对话框
                        dialog.show();
                    }
                });

                Glide.with(getApplicationContext()).load(current_moment.getImages().get(i)).into(imageViewArray[i]);

            }



            if(photo_num<=3) {
                for (int i = photo_num; i < 3; i++) {
                    constraintLayouts[i].setVisibility(View.GONE);
                }

                for (int i = 3; i < 9; i++) {
                    constraintLayouts[i].setVisibility(View.GONE);
                }
            }else if(photo_num<=6){
                for (int i = photo_num; i < 6; i++) {
                    constraintLayouts[i].setVisibility(View.INVISIBLE);
                }

                for (int i = 6; i < 9; i++) {
                    constraintLayouts[i].setVisibility(View.GONE);
                }
            }else if(photo_num<=9){
                for (int i = photo_num; i < 9; i++) {
                    constraintLayouts[i].setVisibility(View.INVISIBLE);
                }
            }
        }

        if(current_moment.getVideos().size()==0){
            video.setVisibility(View.GONE);
        }else{
            video.setVisibility(View.VISIBLE);
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            video.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(current_moment.getVideos().get(0));
            player.setMediaItem(mediaItem);
            player.prepare();
        }

        FlexboxLayout tag_layout=findViewById(R.id.info_tag);

        if(!current_moment.getLocation().equals("null")){
            View tagView = LayoutInflater.from(InfoActivity.this).inflate(R.layout.location_item, tag_layout, false);
            TextView textView = tagView.findViewById(R.id.location_item_text);
            textView.setText(current_moment.getLocation());
            tag_layout.addView(tagView);
        }

        for (String tag : current_moment.getTags()) {
            View tagView = LayoutInflater.from(InfoActivity.this).inflate(R.layout.tag_item, tag_layout, false);
            TextView textView = tagView.findViewById(R.id.tag_item_text);
            textView.setText(tag);
            tag_layout.addView(tagView);
        }










        RecyclerView recyclerView=findViewById(R.id.comment_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        CommentListAdapter adapter=new CommentListAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new CommentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String user_id,String username) throws ExecutionException, InterruptedException {
                //Intent intent = new Intent(getApplicationContext(), PersonActivity.class);


                Intent intent = new Intent(InfoActivity.this, PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("USER_ID", user_id);
                intent.putExtra("BUNDLE_DATA", bundle);
                InfoActivity.this.startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        no_data=findViewById(R.id.info_no_data);
        no_data.setVisibility(View.GONE);



        Log.d("COMMENT SUCC","/posts/comment?id="+current_moment.getId());
        WebUtils.sendGet("/posts/comment?id="+current_moment.getId(), false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {


                List<Comment> newComments=new ArrayList<>();

                Log.d("COMMENT SUCC",json.toString());

                try {
                    Integer comment_count = json.getInt("size");
                    Object msg=json.get("message");

                    if (msg instanceof JSONArray) {
                        JSONArray nestedArray = (JSONArray) msg;

                        // 处理列表中的数据
                        for (int ii = 0; ii < nestedArray.length(); ii++) {
                            Object listItem = nestedArray.get(ii);
                            if(listItem instanceof JSONObject){
                                JSONObject comment=(JSONObject)listItem;
                                String user_id=comment.getString("id");
                                String username=comment.getString("username");
                                String content=comment.getString("text");
                                String time=comment.getString("time");
                                newComments.add(new Comment(user_id,username,content,time));

                                Log.d("COMMENT GET",user_id+" "+content+" "+time);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if(newComments.size()!=0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setSearchUserResults(newComments);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_data.setVisibility(View.VISIBLE);
                        }
                    });
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

        swipeRefreshLayout = findViewById(R.id.infoSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_data.setVisibility(View.GONE);

                Log.d("FRESH","in");

                WebUtils.sendGet("/posts/comment?id="+current_moment.getId(), false, new WebUtils.WebCallback() {
                    @Override
                    public void onSuccess(JSONObject json) {


                        List<Comment> newComments=new ArrayList<>();

                        Log.d("COMMENT SUCC",json.toString());

                        try {
                            Integer comment_count = json.getInt("size");
                            Object msg=json.get("message");

                            if (msg instanceof JSONArray) {
                                JSONArray nestedArray = (JSONArray) msg;

                                // 处理列表中的数据
                                for (int ii = 0; ii < nestedArray.length(); ii++) {
                                    Object listItem = nestedArray.get(ii);
                                    if(listItem instanceof JSONObject){
                                        JSONObject comment=(JSONObject)listItem;
                                        String user_id=comment.getString("id");
                                        String username=comment.getString("username");
                                        String content=comment.getString("text");
                                        String time=comment.getString("time");
                                        newComments.add(new Comment(user_id,username,content,time));

                                        Log.d("COMMENT GET",user_id+" "+content+" "+time);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if(newComments.size()!=0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setSearchUserResults(newComments);
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });

                        }else{

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    no_data.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("ERROR", t.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(JSONObject json) {
                        try {
                            Log.e("FAILURE", json.getString("message"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });






        commentInput = findViewById(R.id.infoTextInputLayout);
        EditText editText = commentInput.getEditText();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    JSONObject json = new JSONObject();
                    try {
                        String comment = editText.getText().toString();
                        json.put("text", comment);
                        json.put("id", current_moment.getId());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    WebUtils.sendPost("/posts/comment/", true, json, new WebUtils.WebCallback() {
                        @Override
                        public void onSuccess(JSONObject json) {

                            //run in UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //swipeRefreshLayout.setRefreshing(true);

                                    Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                        @Override
                        public void onError(Throwable t) {

                        }

                        @Override
                        public void onFailure(JSONObject json) {

                        }
                    });

                    editText.setText("");//清空内容
                }
                return false;
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(player!=null){
            player.release();
        }
        //photo1.setImageBitmap(null);
        //Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }else if(item.getItemId()==R.id.action_share){
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, current_moment.getTopic());
            intent.putExtra(Intent.EXTRA_TEXT, current_moment.getText());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }
}