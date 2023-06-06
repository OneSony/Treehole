package com.example.treehole.activity;

import android.Manifest;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.PhotoListAdapter;
import com.example.treehole.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    private String locationName="";

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVideo;

    private PlayerView videoView;

    RecyclerView recyclerView;

    private Uri videoUri;

    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


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
                    adapter.addUris(uris.get(i));
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

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("SEND_EXIT", false);//默认异常退出
        preferencesEditor.apply();


        Geocoder geocoder = new Geocoder(getApplicationContext());

        double latitude = 39.908860;
        double longitude = 116.397390;

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            String locality = address.getLocality(); // 城市
            String adminArea = address.getAdminArea(); // 省/州
            String country = address.getCountryName(); // 国家
            Log.d("LOCATION", country);
            // 其他地址信息...
        }

        Log.d("LOCATION", "out!");


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

        if (selectFlag == 0 || selectFlag == 1) {

            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            Toast.makeText(getApplicationContext(), "已选择视频，无法同时选择照片", Toast.LENGTH_SHORT).show();
        }

    }

    public void video_click(View view) {

        if (selectFlag == 0 || selectFlag == 2) {
            pickVideo.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                    .build());
        } else {
            Toast.makeText(getApplicationContext(), "已选择照片，无法同时选择视频", Toast.LENGTH_SHORT).show();
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

        if (locationFlag == false) {



            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求获取定位权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);//?
            } else {
                // 已经具有定位权限，可以开始使用定位服务
                TextView locationTextView = findViewById(R.id.edit_location);
                locationTextView.setText("定位中");
                locationTextView.setVisibility(View.VISIBLE);

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
                                                locationFlag = true;
                                                locationTextView.setText(cityName);
                                                locationName=cityName;
                                                Log.d("LOCATION", "City Name: " + cityName);

                                                // 在这里进行UI更新或其他操作
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Log.e("LOCATION", "Error: " + e.getMessage());
                                    locationTextView.setVisibility(View.GONE);
                                    locationFlag=false;
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
        } else {
            TextView locationTextView = findViewById(R.id.edit_location);
            locationTextView.setText("定位中");
            locationTextView.setVisibility(View.GONE);
            locationFlag = false;
            locationName="";
        }
    }
}

