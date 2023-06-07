package com.example.treehole.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.treehole.ChatViewModel;
import com.example.treehole.FileUtils;
import com.example.treehole.R;
import com.example.treehole.WebUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SettingsActivity extends AppCompatActivity {

    static ChatViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        setSupportActionBar(findViewById(R.id.settings_toolbar));
        ActionBar bar=getSupportActionBar();
        bar.setTitle("Settings");
        bar.setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        static ImageView changeProfilePictureImageView;
        static private ActivityResultLauncher<PickVisualMediaRequest> pickImage;
        static Uri newProfilePictureUri;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pickImage = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                //getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (uri != null) {

                    getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    newProfilePictureUri = uri;
                    changeProfilePictureImageView.setImageURI(uri);
                    //Log.d("PhotoPicker", "PATH: " + getRealPathFromUri(getApplicationContext(),uris.get(0)));
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                }
            });
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            findPreference("profile_photo").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("修改头像");



                    // Inflate the custom dialog layout
                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_profile_photo, null);
                    Button button = dialogView.findViewById(R.id.dialog_profile_button);
                    changeProfilePictureImageView = dialogView.findViewById(R.id.change_profile_photo);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(checkFilePermission()==false){
                                Toast.makeText(getContext(), "请授予文件读写权限", Toast.LENGTH_SHORT).show();
                            }else {
                            /*ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                                // Callback is invoked after the user selects a media item or closes the
                                // photo picker.
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    Uri uri = result.getData().getData();
                                    //getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    imageView.setImageURI(uri);
                                    //Log.d("PhotoPicker", "PATH: " + getRealPathFromUri(getApplicationContext(),uris.get(0)));
                                    Log.d("PhotoPicker", "Selected URI: " + uri);
                                } else {
                                    Log.d("PhotoPicker", "No media selected");
                                }
                            });*/
                                //Toast.makeText(getContext(), "打开相册", Toast.LENGTH_SHORT).show();
                                pickImage.launch(new PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                        .build());
                            }
                        }
                    });

                    builder.setView(dialogView);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Handle OK button click

                            // Do something with the entered text
                            // ...
                            RequestBody imageBody = RequestBody.create(new File(getPathFromUri(getContext(),newProfilePictureUri)), MediaType.parse("image/jpeg"));
                            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("image", "profile_picture", imageBody);

                            //json.put("profile_picture", )
                            WebUtils.sendPost("/users/change_profile_picture/", true, requestBodyBuilder, new WebUtils.WebCallback() {
                                @Override
                                public void onSuccess(JSONObject json) {
                                    Log.d("CHANGEPROFILE", "successfully changed profile picture");
                                }

                                @Override
                                public void onError(Throwable t) {
                                    Log.d("CHANGEPROFILE", t.getMessage());
                                }

                                @Override
                                public void onFailure(JSONObject json) {
                                    Log.d("CHANGEPROFILE", json.optString("message", "onFailure"));
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle Cancel button click
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });

            findPreference("username").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("修改用户名");

                    // 创建一个 EditText 作为对话框中的文本输入框
                    final EditText editText = new EditText(getActivity());
                    editText.setSingleLine(true);
                    builder.setView(editText);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String enteredText = editText.getText().toString();
                            // 在这里获取到文本框的字符串 enteredText，并进行相应的操作
                            Toast.makeText(getContext(), "输入的文本：" + enteredText, Toast.LENGTH_SHORT).show();
                            //连接服务器！！！
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("username", enteredText);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            WebUtils.sendPost("/users/change_username/", true, jsonObject, new WebUtils.WebCallback() {
                                @Override
                                public void onSuccess(JSONObject json) {
                                    Log.d("CHANGEUSERNAME", "successful");
                                }

                                @Override
                                public void onError(Throwable t) {
                                    Log.d("CHANGEUSERNAME", t.getMessage());
                                }

                                @Override
                                public void onFailure(JSONObject json) {
                                    Log.d("CHANGEUSERNAME", json.optString("message", "onFailure"));
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 处理点击取消按钮的逻辑
                        }
                    });

                    builder.show();

                    return false;
                }
            });

            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("修改个人介绍");

                    // 创建一个 EditText 作为对话框中的文本输入框
                    final EditText editText = new EditText(getActivity());
                    editText.setSingleLine(true);
                    builder.setView(editText);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String enteredText = editText.getText().toString();
                            // 在这里获取到文本框的字符串 enteredText，并进行相应的操作
                            Toast.makeText(getContext(), "输入的文本：" + enteredText, Toast.LENGTH_SHORT).show();
                            //连接服务器！！！
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("description", enteredText);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            WebUtils.sendPost("/users/change_description/", true, jsonObject, new WebUtils.WebCallback() {
                                @Override
                                public void onSuccess(JSONObject json) {
                                    Log.d("CHANGEDESCRIPTION", "successful");
                                }

                                @Override
                                public void onError(Throwable t) {
                                    Log.d("CHANGEDESCRIPTION", t.getMessage());
                                }

                                @Override
                                public void onFailure(JSONObject json) {
                                    Log.d("CHANGEDESCRIPTION", json.optString("message", "onFailure"));
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 处理点击取消按钮的逻辑
                        }
                    });

                    builder.show();

                    return false;
                }
            });



            findPreference("password").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("修改密码");

                    // Inflate the custom dialog layout
                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password, null);
                    builder.setView(dialogView);

                    TextInputLayout oldPasswordInput = dialogView.findViewById(R.id.old_password_input);
                    TextInputLayout newPasswordInput = dialogView.findViewById(R.id.new_password_input);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle OK button click

                            // Do something with the entered text
                            // ...
                            String old_password = oldPasswordInput.getEditText().getText().toString();
                            String new_password = newPasswordInput.getEditText().getText().toString();
                            JSONObject passwords = new JSONObject();
                            try {
                                passwords.put("old", old_password);
                                passwords.put("new", new_password);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            WebUtils.sendPost("/users/change_password/", true, passwords, new WebUtils.WebCallback() {
                                @Override
                                public void onSuccess(JSONObject json) {
                                    Log.d("CHANGEPASSWORD", "successful");
                                }

                                @Override
                                public void onError(Throwable t) {
                                    Log.d("CHANGEPASSWORD", t.getMessage());
                                }

                                @Override
                                public void onFailure(JSONObject json) {
                                    Log.d("CHANGEPASSWORD", json.optString("message", "onFailure"));
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle Cancel button click
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });

            /*
            findPreference("password").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getContext(), "password changed and not memorized", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/

            findPreference("login_out").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("退出登录")
                            .setMessage("确定要退出登录吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(),"退出成功",Toast.LENGTH_SHORT).show();
                                    WebUtils.setLogIn(false);

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 处理点击取消按钮的逻辑
                                }
                            })
                            .show();

                    return false;
                }
            });

            findPreference("delete_message").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("清空所有聊天记录")
                            .setMessage("确定要清空所有聊天记录吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    viewModel.deleteAllMessage();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 处理点击取消按钮的逻辑
                                }
                            })
                            .show();

                    return false;
                }
            });

        }

        /*
        private String getPathFromUri(Uri uri) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

            String path = null;
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
                cursor.close();
            }

            return path;
        }*/

        public String getPathFromUri(Context context, Uri uri){

            String filePath = FileUtils.getUriFilePath(context, uri);
            if(filePath !=null){
                Log.d("PATH", filePath);
                return filePath;
            } else {
                Log.d("PATH", "null");
                return "";
            }

        }

        private boolean checkFilePermission(){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 如果未被授予权限，需要请求权限
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3);
                return false;
            } else {
                return true;
            }
        }
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