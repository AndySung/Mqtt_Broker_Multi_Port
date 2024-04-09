package com.soft.nice.mqttservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ServiceMainActivity extends Activity {
    private static final String TAG = "NiceCIC>>>>>>>>ServiceMainActivity";
    //记录是否第一次启动
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_FIRST_RUN = "firstRun";
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 检查是否是第一次运行
        if (isFirstRun()) {
            // 执行只需执行一次的操作
            MyApp.writeToAclFile(MyApp.getAclFile());
            MyApp.writeToPasswordFile(MyApp.getPasswordFile());
            // 设置标记位，表示已经执行过
            setFirstRun(false);
        }

        getFilePermission();

        startDefaultPort();
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
    }

    private void getFilePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // 已经具有访问外部存储权限，可以进行文件操作
                // 在此处执行文件访问操作
            } else {
                // 请求访问外部存储权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.parse("package:" + getPackageName());
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private boolean isFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_RUN, true);
    }

    private void setFirstRun(boolean firstRun) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_FIRST_RUN, firstRun);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startDefaultPort(){
//        Intent intent = new Intent();
//        intent.setClass(ServiceMainActivity.this, MQTTService.class);

        startUseService(ServiceMainActivity.this, MQTTService.class);
//        startService(intent);
    }

    public static void startUseService(Context ctx, Class cls) {
        Intent intent = new Intent(ctx, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable("config",MyApp.defaultConfig("1883", MyApp.getConfFile()));
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(intent);
        } else {
            ctx.startService(intent);
        }
    }

    /*private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "连接成功");
            // 当Service连接建立成功后，提供给客户端与Service交互的对象（根据Android Doc翻译的，不知道准确否。。。。）
            myService = ((MQTTService.LocalBinder) service).getService();
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "断开连接");
            myService = null;
        }
    };*/
}