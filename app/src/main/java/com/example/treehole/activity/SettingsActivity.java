package com.example.treehole.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.treehole.ChatViewModel;
import com.example.treehole.R;
import com.example.treehole.WebUtils;

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
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "打开相册", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setView(dialogView);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle OK button click

                            // Do something with the entered text
                            // ...
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

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle OK button click

                            // Do something with the entered text
                            // ...
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