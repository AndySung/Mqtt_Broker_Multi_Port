package com.soft.nice.mqttservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServiceMainActivity extends Activity {
    private static final String TAG = "ServiceMainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDefaultPort();
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startDefaultPort(){
        Intent intent = new Intent();
        intent.setClass(ServiceMainActivity.this, MQTTService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("config",MyApp.defaultConfig("1883", MyApp.getConfFile()));
        intent.putExtras(bundle);
        startService(intent);
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