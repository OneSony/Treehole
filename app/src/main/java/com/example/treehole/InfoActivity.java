package com.example.treehole;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class InfoActivity extends AppCompatActivity {
    private TextView topic_box;
    private TextView main_box;

    private TextView auth_box;
    private TextView date_box;
    private ImageView profile_photo;

    private dot curr_data;

    public ImageView photo1;

    public ImageView photo2;

    public ImageView photo3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setSupportActionBar(findViewById(R.id.info_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle=getIntent().getBundleExtra("BUNDLE_DATA");
        if (bundle != null) {
            curr_data = (dot) bundle.getSerializable("DATA");

            topic_box = findViewById(R.id.topic_box);
            main_box = findViewById(R.id.main_box);
            auth_box = findViewById(R.id.auth_box);
            date_box = findViewById(R.id.date_box);

            photo1 = findViewById(R.id.photo1);
            photo2 = findViewById(R.id.photo2);
            photo3 = findViewById(R.id.photo3);

            profile_photo = findViewById(R.id.profile);


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

            if(curr_data.isPath_flag()==true){
                photo1.setVisibility(View.VISIBLE);
                Log.d("PATH","in!!");

                Glide.with(this).load("https://rickyvu.pythonanywhere.com/static/images/test1.png").into(photo1);

                Log.d("PATH","out!!");

                /*File imgFile = new File(curr_data.getPhoto_path());
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photo1.setImageBitmap(bitmap);
                }*/
                //holder.photo1.setImageResource(R.drawable.photo1);
            }
        }

        /*photo1.setImageResource(R.drawable.photo);
        photo2.setImageResource(R.drawable.photo);
        photo3.setImageResource(R.drawable.photo);*/
    }

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
        switch (item.getItemId()){
            // android.R.id.home 这个是获取ids.xml页面的返回箭头，项目自带的，要加上android
            case android.R.id.home:
                // 返回
                this.finish();
                // 结束
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}