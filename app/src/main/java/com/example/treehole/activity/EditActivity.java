package com.example.treehole.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.FileUtils;
import com.example.treehole.PhotoListAdapter;
import com.example.treehole.R;
import com.example.treehole.WebUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class EditActivity extends AppCompatActivity {


    private TextInputLayout topicInputLayout;
    private TextInputLayout textInputLayout;
    private ImageView photoView;
    private CardView cardView;
    private ImageButton photoButton;

    PhotoListAdapter adapter;


    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.Treehole";

    private int selectFlag = 0;//当前选择了照片还是视频，0无，1照片，2视频
    private boolean locationFlag = false;

    private boolean markdownFlag = false;

    private String locationName = "";

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVideo;

    private PlayerView videoView;

    RecyclerView recyclerView;

    private Uri videoUri;

    private ExoPlayer player;

    private ProgressBar progressBar;

    private boolean markdown_realtime_preview;

    public FlexboxLayout edit_tag_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        markdown_realtime_preview = sharedPreferences.getBoolean("markdown_realtime_preview", true);

        if(markdown_realtime_preview==false){
            Button button = findViewById(R.id.markdown_refresh_button);
            button.setVisibility(View.VISIBLE);
        }

        progressBar=findViewById(R.id.edit_location_progress);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(findViewById(R.id.edit_toolbar));
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        TextView locationTextView = findViewById(R.id.edit_location);
        locationTextView.setVisibility(View.GONE);

        videoView = findViewById(R.id.edit_video);
        recyclerView = findViewById(R.id.photo_recyclerview);

        videoView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 设置网格布局，3表示每行显示的列数

// 创建适配器并设置给RecyclerView
        adapter = new PhotoListAdapter(); // 替换为您自己的适配器和数据
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

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    // 处理单击事件
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = recyclerView.getChildAdapterPosition(childView);
                        // 处理点击位置的操作
                    }
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // 处理双击事件
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = recyclerView.getChildAdapterPosition(childView);
                        // 处理双击位置的操作
                        Toast.makeText(EditActivity.this, "双击了第" + position + "个项目", Toast.LENGTH_SHORT).show();
                        adapter.deleteItem(position);
                        if (adapter.getItemCount() == 0) {
                            setView(0);
                        } else {
                            setView(1);
                        }
                    }
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // 不做任何操作
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // 不做任何操作
            }
        });


        topicInputLayout = findViewById(R.id.topic_input);
        textInputLayout = findViewById(R.id.text_input);
        photoButton = (ImageButton) findViewById(R.id.photo_button);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uris.size() != 0) {

                for (int i = 0; i < uris.size(); i++) {
                    getContentResolver().takePersistableUriPermission(uris.get(i), Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    int flag=adapter.addUris(uris.get(i));

                    if(flag==1){
                        Toast.makeText(this, "最多只能选择9张图片", Toast.LENGTH_SHORT).show();
                        break;
                    }else if(flag==2){
                        Toast.makeText(this, "已忽略重复照片", Toast.LENGTH_SHORT).show();
                    }
                }

                setView(1);

                //Log.d("PhotoPicker", "PATH: " + getRealPathFromUri(getApplicationContext(),uris.get(0)));
                Log.d("PhotoPicker", "Selected URI: " + uris.get(0));
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        pickVideo = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.

            if (uri != null) {

                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                videoUri = uri;

                player = new ExoPlayer.Builder(getApplicationContext()).build();
                videoView.setPlayer(player);
                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(videoUri);
// Set the media item to be played.
                player.setMediaItem(mediaItem);
// Prepare the player.
                player.prepare();
// Start the playback.
                //player.play();

                setView(2);

            } else {
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // 在双击事件发生时执行关闭操作
                // 这里可以添加您的关闭逻辑，例如停止播放、隐藏PlayerView等
                setView(0);
                videoUri = null;
                player.release();
                return true;
            }
        });

// 将触摸事件分发给GestureDetector处理
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        if (mPreferences.getBoolean("SEND_EXIT", true) == false) {//异常退出恢复
            String topic = mPreferences.getString("TOPIC_STR", "");
            String text = mPreferences.getString("TEXT_STR", "");
            String uris_str = mPreferences.getString("URIS", "");
            selectFlag = mPreferences.getInt("SELECT_FLAG", 0);

            if (!topic.equals("") || !text.equals("") || !uris_str.equals("")) {
                topicInputLayout.getEditText().setText(topic);
                textInputLayout.getEditText().setText(text);

                if (!uris_str.equals("")) {
                    List<String> uris_str_list = new ArrayList<>(Arrays.asList(uris_str.split(",")));

                    List<Uri> uris = new ArrayList<>();

                    for (String urlString : uris_str_list) {
                        Uri uri = Uri.parse(urlString);
                        uris.add(uri);
                    }

                    if (selectFlag == 1) {
                        adapter.setUris(uris);//adapter会自动更新
                    } else if (selectFlag == 2) {
                        videoUri = uris.get(0);//video需要提醒
                        player = new ExoPlayer.Builder(getApplicationContext()).build();
                        videoView.setPlayer(player);
                        MediaItem mediaItem = MediaItem.fromUri(videoUri);
                        player.setMediaItem(mediaItem);
                        player.prepare();
                    }
                }

                Toast toast = Toast.makeText(this, "已恢复草稿", Toast.LENGTH_SHORT);
                toast.show();
            }

            setView(selectFlag);
        }


        ImageButton markdown_button = findViewById(R.id.markdown_button);
        markdown_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout markdownLayout=findViewById(R.id.markdown_layout);
                TextView markdownTextView = findViewById(R.id.markdown_textview);

                if(markdownFlag==false) {
                    markdownFlag=true;
                    Markwon markwon = Markwon.create(getApplicationContext());

                    markdownTextView.setMovementMethod(new ScrollingMovementMethod()); // 启用滚动

                    markwon.setMarkdown(markdownTextView, textInputLayout.getEditText().getText().toString());//先把已经有的放进去

                    if(markdown_realtime_preview==true) {

                        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                                // 在文本改变之前执行的操作
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                                // 在文本改变时执行的操作
                                String inputText = charSequence.toString();
                                markwon.setMarkdown(markdownTextView, inputText);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                // 在文本改变之后执行的操作
                            }

                        });
                    }

                    markdownLayout.setVisibility(View.VISIBLE);
                }else{
                    markdownFlag=false;

                    markdownLayout.setVisibility(View.GONE);

                    if(markdown_realtime_preview==true) {
                        textInputLayout.getEditText().addTextChangedListener(null);
                    }
                }

            }
        });


        edit_tag_layout=findViewById(R.id.edit_tag_layout);


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
        switch (item.getItemId()) {
            // android.R.id.home 这个是获取ids.xml页面的返回箭头，项目自带的，要加上android
            case android.R.id.home:
                // 返回
                this.finish();
                // 结束
                return true;
            case R.id.action_send:
                if (send() == true) {
                    this.finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void send_click(View view) {
        if (send() == true) {
            this.finish();
        }
    }

    public void photo_click(View view) {

        if(checkFilePermission()==true) {

            if (selectFlag == 0 || selectFlag == 1) {

                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        }else{
            Toast toast = Toast.makeText(this, "请授予文件读写权限", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void video_click(View view) {

        if(checkFilePermission()==true) {

            if (selectFlag == 0 || selectFlag == 2) {
                pickVideo.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                        .build());
            }
        }else{
            Toast toast = Toast.makeText(this, "请授予文件读写权限", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        String topic = topicInputLayout.getEditText().getText().toString();
        String text = textInputLayout.getEditText().getText().toString();
        String uris_str = "";

        uris_str = TextUtils.join(",", getUris());

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("TOPIC_STR", topic);
        preferencesEditor.putString("TEXT_STR", text);
        preferencesEditor.putString("URIS", uris_str);
        preferencesEditor.putInt("SELECT_FLAG", selectFlag);

        preferencesEditor.apply();

        /*if(mPreferences.getBoolean("SEND_EXIT",false)!=true&&(topic.length()!=0||text.length()!=0)){//发送完了true就不需要保存了
            Toast toast=Toast.makeText(this,"已保存草稿",Toast.LENGTH_SHORT);
            toast.show();
        }*/
    }

    private boolean send() {
        String topic = topicInputLayout.getEditText().getText().toString();
        String text = textInputLayout.getEditText().getText().toString();

        if (topic.equals("") && text.equals("")) {
            Toast toast = Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (topic.equals("")) {
            Toast toast = Toast.makeText(this, "请输入主题", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (text.equals("")) {
            Toast toast = Toast.makeText(this, "请输入正文", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }


        Toast toast = Toast.makeText(this, "已发布", Toast.LENGTH_SHORT);
        toast.show();
        /*Toast toast=Toast.makeText(this,mPreferences.getString("TOPIC_STR","none"),Toast.LENGTH_SHORT);
        toast.show();*/

        FileUtils.CompressionHandler.Builder compressionHandlerBuilder = new FileUtils.CompressionHandler.Builder(getApplicationContext());
        for (String uriString: getUris()){
            if (selectFlag == 1) {
                compressionHandlerBuilder.add(Uri.parse(uriString), FileUtils.MEDIA_TYPE.IMAGE);
            }
            if (selectFlag == 2) {
                compressionHandlerBuilder.add(Uri.parse(uriString), FileUtils.MEDIA_TYPE.VIDEO);
            }
        }
        compressionHandlerBuilder.addCallback(new FileUtils.CompressionThreadCallback() {
            @Override
            public void onResult(List<File> imageFiles, List<File> videoFiles) {
                Log.d("COMPRESSIONDONE", "RESULT: images:"+imageFiles.size()+"|videos:"+videoFiles.size());

                // Setup a multipart body
                MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                // Add title and text to it
                RequestBody titleBody = RequestBody.create(topicInputLayout.getEditText().getText().toString(), MediaType.parse("text/plain"));
                RequestBody textBody = RequestBody.create(textInputLayout.getEditText().getText().toString(), MediaType.parse("text/plain"));
                requestBodyBuilder.addFormDataPart("title", null, titleBody);
                requestBodyBuilder.addFormDataPart("text_content", null, textBody);




                List<String> tagsList= getTags();
                if(tagsList.size()!=0) {
                    String[] tags = tagsList.toArray(new String[0]);
                    // Encode the data as JSON
                    RequestBody tagsBody = RequestBody.create(Arrays.toString(tags), MediaType.parse("application/json"));
                    requestBodyBuilder.addFormDataPart("tags", null, tagsBody);
                }

                if(locationFlag==true) {
                    RequestBody locationBody = RequestBody.create(locationName, MediaType.parse("text/plain"));
                    requestBodyBuilder.addFormDataPart("location", null, locationBody);
                }



                // Add image or video to it
                for (int i = 0; i < imageFiles.size(); i++) {
                    RequestBody imageBody = RequestBody.create(imageFiles.get(i), MediaType.parse("image/jpeg"));
                    requestBodyBuilder.addFormDataPart("image-" + (i + 1), imageFiles.get(i).getName(), imageBody);
                }
                for (int i = 0; i < videoFiles.size(); i++) {
                    RequestBody imageBody = RequestBody.create(videoFiles.get(i), MediaType.parse("image/jpeg"));
                    requestBodyBuilder.addFormDataPart("video-" + (i + 1), videoFiles.get(i).getName(), imageBody);
                }


                WebUtils.sendPost("/posts/post/", true, requestBodyBuilder, new WebUtils.WebCallback() {
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

        FileUtils.CompressionHandler compressionHandler = compressionHandlerBuilder.build();
        compressionHandler.start();

        Log.d("SEND", "TEST");

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("SEND_EXIT", true);//正常发送
        preferencesEditor.apply();
        return true;
    }

    public void delete_click(View view) {
        cardView.setVisibility(View.GONE);
        //photo_path="";
        //photoView.setImageBitmap(null);
    }

    public void setView(int flag) {
        if (flag == 0) {//no data
            selectFlag = 0;
            recyclerView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            Log.d("setView", "0");
        } else if (flag == 1) {//photo
            selectFlag = 1;
            recyclerView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Log.d("setView", "1");
        } else if (flag == 2) {//video
            selectFlag = 2;
            recyclerView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            Log.d("setView", "2");
        }
    }

    public List<String> getUris() {
        if (selectFlag == 1) {//photo
            return adapter.getUris();
        } else if (selectFlag == 2) {
            List<String> uris = new ArrayList<>();
            uris.add(videoUri.toString());
            return uris;
        } else {
            List<String> uris = new ArrayList<>();
            return uris;
        }
    }

    public void location_click(View view) {



        if (locationFlag == false) {//没有添加定位信息


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求获取定位权限

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            } else {

                view.setClickable(false);

                TextView locationTextView = findViewById(R.id.edit_location);
                locationTextView.setText("定位中");

                locationTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                LocationManager locationManager;
                LocationListener locationListener;

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // 初始化 LocationListener
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        Thread locationThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 调用定位服务获取位置信息
                                // 这里假设已经获取到了位置信息，包括经纬度

                                // 查询地理位置
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String cityName = address.getLocality(); // 获取城市名称

                                        // 在新线程中获取到城市名称后，通过回调将结果返回

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 在主线程中处理返回的城市名称

                                                locationTextView.setVisibility(View.VISIBLE);
                                                locationFlag = true;
                                                locationTextView.setText(cityName);
                                                locationName = cityName;

                                                progressBar.setVisibility(View.GONE);
                                                Log.d("LOCATION", "City Name: " + cityName);

                                                CardView locationCardView=findViewById(R.id.cardView7);
                                                locationCardView.setClickable(true);

                                                // 在这里进行UI更新或其他操作
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Log.e("LOCATION", "Error: " + e.getMessage());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 在主线程中处理返回的城市名称
                                            locationTextView.setText("定位失败");
                                            locationTextView.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            locationFlag = false;

                                            CardView locationCardView=findViewById(R.id.cardView7);
                                            locationCardView.setClickable(true);

                                            // 在这里进行UI更新或其他操作
                                        }
                                    });

                                    e.printStackTrace();
                                }
                            }
                        });
                        locationThread.start(); // 启动线程
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // 处理位置提供者状态更改
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // 处理位置提供者启用
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // 处理位置提供者禁用
                    }
                };

                // 请求一次位置更新
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                }

            }
        } else {//添加了定位信息，要取消
            TextView locationTextView = findViewById(R.id.edit_location);
            locationTextView.setText("定位中");
            locationTextView.setVisibility(View.GONE);
            locationFlag = false;
            locationName = "";
        }
    }

/*
    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        String path = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }

        return path;
    }

*/

    public String getPathFromUri(Context context,Uri uri){

        String filePath = FileUtils.getUriFilePath(context, uri);
        if(filePath !=null){
            Log.d("PATH", filePath);
            return filePath;
        } else {
            Log.d("PATH", "null");
            return "";
        }

    }

    // Similar to getUris, but converts uri to path first using getPathFromUri
    public List<String> getPaths() {
        if(selectFlag==1){//photo
            List<String> paths = new ArrayList<>();
            for (String uriString:adapter.getUris()) {
                paths.add(getPathFromUri(getApplicationContext(),Uri.parse(uriString)));
                if(getPathFromUri(getApplicationContext(),Uri.parse(uriString))==null) {
                    Log.d("PATH", "NULL");
                }
            }
            return paths;
        }else if(selectFlag==2){
            List<String> paths=new ArrayList<>();
            paths.add(getPathFromUri(getApplicationContext(),videoUri));
            return paths;
        }else {
            List<String> uris=new ArrayList<>();
            return uris;
        }
    }


    private boolean checkFilePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果未被授予权限，需要请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    3);
            return false;
        } else {
            return true;
        }
    }

    public void markdown_refresh_click(View view) {
        if(markdownFlag==true) {
            TextView markdownTextView = findViewById(R.id.markdown_textview);
            Markwon markwon = Markwon.create(getApplicationContext());
            markdownTextView.setMovementMethod(new ScrollingMovementMethod()); // 启用滚动
            markwon.setMarkdown(markdownTextView, textInputLayout.getEditText().getText().toString());//先把已经有的放进去
        }
    }

    public void tag_click(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加标签");  // 设置对话框标题

        // 创建文本输入框
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addTags(editText.getText().toString());
                v.setText("");
                return true;
            }
        });
        builder.setView(editText);  // 将文本输入框设置为对话框的内容

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = editText.getText().toString();

                addTags(inputText);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 在这里处理取消按钮的点击事件
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    List<String> getTags() {
        List<String> textList = new ArrayList<>();

        for (int i = 0; i < edit_tag_layout.getChildCount(); i++) {
            View childView = edit_tag_layout.getChildAt(i);
            if (childView instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) childView;
                TextView textView = frameLayout.findViewById(R.id.tag_item_text);
                if (textView != null) {
                    String text = textView.getText().toString();
                    textList.add(text);
                }
            }
        }

        return textList;
    }

    private void addTags(String tag){

        if(tag.equals("")||tag.matches("\\s*")==true){
            return;
        }

        List<String> exitsTags = getTags();
        if(exitsTags.contains(tag)) {
            Toast.makeText(EditActivity.this, "标签已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用正确的上下文获取布局视图
        View tagView = LayoutInflater.from(EditActivity.this).inflate(R.layout.tag_item, edit_tag_layout, false);
        TextView textView = tagView.findViewById(R.id.tag_item_text);
        textView.setText(tag);
        tagView.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;
            private static final long DOUBLE_CLICK_TIME_DELTA = 300; // 双击间隔时间阈值

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long clickTime = System.currentTimeMillis();
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        // 双击事件
                        edit_tag_layout.removeView(tagView); // 移除被双击的标签视图
                    }
                    lastClickTime = clickTime;
                }
                return true;
            }
        });
        edit_tag_layout.addView(tagView);
    }


}

