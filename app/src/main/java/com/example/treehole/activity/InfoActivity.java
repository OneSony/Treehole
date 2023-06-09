package com.example.treehole.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.UserUtils;
import com.example.treehole.room.Moment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class InfoActivity extends AppCompatActivity {
    private TextView topic_box;
    private TextView main_box;

    private TextView auth_box;
    private TextView date_box;
    private ImageView profile_photo;

    private Moment current_moment;


    private ImageView[] imageViewArray;
    private ConstraintLayout[] constraintLayouts;

    private LinearLayout photos;

    private PlayerView video;

    private String user_id;


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

            ExoPlayer player;
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            video.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(current_moment.getVideos().get(0));
            player.setMediaItem(mediaItem);
            player.prepare();
        }

            /*

            curr_data = (dot) bundle.getSerializable("DATA");

            topic_box = findViewById(R.id.topic_box);
            main_box = findViewById(R.id.main_box);
            auth_box = findViewById(R.id.auth_box);
            date_box = findViewById(R.id.date_box);

            photo1 = findViewById(R.id.photo1);
            photo2 = findViewById(R.id.photo2);
            photo3 = findViewById(R.id.photo3);



            topic_box.setText(curr_data.getTopic());
            main_box.setText(curr_data.getText());
            auth_box.setText(curr_data.getAuth());
            date_box.setText(curr_data.getDate());

            profile_photo.setImageResource(curr_data.getProfile_index());


            photo1.setVisibility(View.GONE);
            photo2.setVisibility(View.GONE);
            photo3.setVisibility(View.GONE);

            int photo_num = curr_data.getPhoto_num();
            if (photo_num == 1) {
                photo1.setVisibility(View.VISIBLE);
                photo1.setImageResource(curr_data.getPhoto_index(0));
            } else if (photo_num == 2) {
                photo1.setVisibility(View.VISIBLE);
                photo1.setImageResource(curr_data.getPhoto_index(0));

                photo2.setVisibility(View.VISIBLE);
                photo2.setImageResource(curr_data.getPhoto_index(1));
            } else if (photo_num == 3) {
                photo1.setVisibility(View.VISIBLE);
                photo1.setImageResource(curr_data.getPhoto_index(0));

                photo2.setVisibility(View.VISIBLE);
                photo2.setImageResource(curr_data.getPhoto_index(1));

                photo3.setVisibility(View.VISIBLE);
                photo3.setImageResource(curr_data.getPhoto_index(2));
            }


             */

            /*

            if(curr_data.isPath_flag()==true){
                photo1.setVisibility(View.VISIBLE);
                Log.d("PATH","in!!");

                Glide.with(this).load("https://rickyvu.pythonanywhere.com/static/images/test1.png").into(photo1);

                Log.d("PATH","out!!");*/


                /*File imgFile = new File(curr_data.getPhoto_path());
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photo1.setImageBitmap(bitmap);
                }*/
                //holder.photo1.setImageResource(R.drawable.photo1);
            }

        /*photo1.setImageResource(R.drawable.photo);
        photo2.setImageResource(R.drawable.photo);
        photo3.setImageResource(R.drawable.photo);*/
    //}

    /*public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putSerializable("SAVED_DATA",);
    }*/

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