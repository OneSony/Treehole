package com.example.treehole;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class EditActivity extends AppCompatActivity {

    private TextInputLayout topicInputLayout;
    private TextInputLayout textInputLayout;
    private ImageView photoView;
    private CardView cardView;
    private ImageButton photoButton;

    private SharedPreferences mPreferences;
    private String sharedPrefFile ="com.example.android.Treehole";

    private String photo_path="";

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        setSupportActionBar(findViewById(R.id.edit_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);


        RecyclerView recyclerView = findViewById(R.id.photo_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 设置网格布局，3表示每行显示的列数

// 创建适配器并设置给RecyclerView
        PhotoListAdapter adapter = new PhotoListAdapter(); // 替换为您自己的适配器和数据
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    // 在拖动开始时应用动画效果
                    viewHolder.itemView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // 在拖动结束后清除动画效果
                viewHolder.itemView.animate().scaleX(1f).scaleY(1f).setDuration(200).start();

            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                // 处理项目移动事件
                adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // 处理项目滑动事件（如果需要）
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true; // 允许长按拖动
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.addUris(Uri.parse("1"));
        adapter.addUris(Uri.parse("2"));
        adapter.addUris(Uri.parse("3"));
        adapter.addUris(Uri.parse("4"));





        topicInputLayout=findViewById(R.id.topic_input);
        textInputLayout=findViewById(R.id.text_input);
        photoView=findViewById(R.id.send_photo_view);
        photoButton= (ImageButton) findViewById(R.id.photo_button);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), uris -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uris.size() != 0) {
                        photoView.setImageURI(uris.get(0));
                        cardView.setVisibility(View.VISIBLE);
                        photo_path="NULL";
                        /*Uri photoUri = uri;
                        Log.d("PHOTO",String.valueOf(photoUri));
                        //photoView.setImageURI(photoUri);

                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(photoUri, projection, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        photo_path = cursor.getString(column_index);
                        cursor.close();

                        File imgFile = new File(photo_path);
                        if(imgFile.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            photoView.setImageBitmap(bitmap);
                            cardView.setVisibility(View.VISIBLE);
                        }*/
                        Log.d("PhotoPicker", "PATH: " + getRealPathFromUri(getApplicationContext(),uris.get(0)));
                        Log.d("PhotoPicker", "Selected URI: " + uris.get(0));
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        cardView=findViewById(R.id.send_card_view);
        cardView.setVisibility(View.GONE);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        if(mPreferences.getBoolean("SEND_EXIT",true)==false) {//异常退出恢复
            String topic = mPreferences.getString("TOPIC_STR", "");
            String text = mPreferences.getString("TEXT_STR", "");
            photo_path = mPreferences.getString("PHOTO_PATH","");

            if(!topic.equals("") || !text.equals("")|| !photo_path.equals("")){
                topicInputLayout.getEditText().setText(topic);
                textInputLayout.getEditText().setText(text);

                if(!photo_path.equals("")){
                    File imgFile = new File(photo_path);
                    if(imgFile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        photoView.setImageBitmap(bitmap);
                        cardView.setVisibility(View.VISIBLE);
                    }
                }

                Toast toast=Toast.makeText(this,"已恢复草稿",Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("SEND_EXIT", false);//默认异常退出
        preferencesEditor.apply();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
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
            case R.id.action_send:
                if(send()==true){
                    this.finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void send_click(View view) {
        if(send()==true){
            this.finish();
        }
    }

    public void photo_click(View view) {


        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());


        /*
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 0);*/

        //API 33
        /*Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        startActivityForResult(intent, 0);*/

        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择照片"), 0);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            // Handle error
            return;
        }

        switch (requestCode) {
            case 0:


                /*
                Uri selectedImageUri = getPermanentUri(data.getData());

                try {
                    Log.d("PHOTO",String.valueOf(selectedImageUri));
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    photoView.setImageBitmap(bitmap);
                    cardView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                */


                Uri photoUri = data.getData();
                Log.d("PHOTO",String.valueOf(photoUri));
                //photoView.setImageURI(photoUri);

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(photoUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                photo_path = cursor.getString(column_index);
                cursor.close();

                File imgFile = new File(photo_path);
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photoView.setImageBitmap(bitmap);
                    cardView.setVisibility(View.VISIBLE);
                }

                return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String topic=topicInputLayout.getEditText().getText().toString();
        String text=textInputLayout.getEditText().getText().toString();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("TOPIC_STR", topic);
        preferencesEditor.putString("TEXT_STR", text);
        preferencesEditor.putString("PHOTO_PATH", photo_path);
        preferencesEditor.apply();

        /*if(mPreferences.getBoolean("SEND_EXIT",false)!=true&&(topic.length()!=0||text.length()!=0)){//发送完了true就不需要保存了
            Toast toast=Toast.makeText(this,"已保存草稿",Toast.LENGTH_SHORT);
            toast.show();
        }*/
    }

    private boolean send(){
        String topic=topicInputLayout.getEditText().getText().toString();
        String text=textInputLayout.getEditText().getText().toString();

        if(topic.equals("")&&text.equals("")){
            Toast toast=Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(topic.equals("")){
            Toast toast=Toast.makeText(this,"请输入主题",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(text.equals("")){
            Toast toast=Toast.makeText(this,"请输入正文",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        application app=(application) getApplication();
        if(!photo_path.equals("")) {
            app.data_list.insert(topicInputLayout.getEditText().getText().toString(), textInputLayout.getEditText().getText().toString(), "我", app.getID("user1"),photo_path);
            //app.data_list.insert(topicInputLayout.getEditText().getText().toString(), textInputLayout.getEditText().getText().toString(), "我", app.getID("user1"));

        }else{
            app.data_list.insert(topicInputLayout.getEditText().getText().toString(), textInputLayout.getEditText().getText().toString(), "我", app.getID("user1"));
        }

        Toast toast=Toast.makeText(this,"已发布",Toast.LENGTH_SHORT);
        toast.show();
        /*Toast toast=Toast.makeText(this,mPreferences.getString("TOPIC_STR","none"),Toast.LENGTH_SHORT);
        toast.show();*/

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("SEND_EXIT", true);//正常发送
        preferencesEditor.apply();
        return true;
    }

    public void delete_click(View view) {
        cardView.setVisibility(View.GONE);
        photo_path="";
        photoView.setImageBitmap(null);
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}

