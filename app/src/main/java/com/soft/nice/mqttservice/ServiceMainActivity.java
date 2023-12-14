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
//    private Button startBtn;
//    private Button stopBtn;
//    private Button bindBtn;
//    private Button unBindBtn;
    IntentFilter intentFilter;
//    File confFile, confFile8882, confFile8883, passwordFile, aclFile;
    private static final String TAG = "ServiceMainActivity";
    private MQTTService myService;

//    private final BroadcastReceiver mReceiverBroadCast = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String msg = intent.getStringExtra("msg");
//            Intent IntentPortOne = new Intent();
//            IntentPortOne.setClass(ServiceMainActivity.this, MQTTPortOneService.class);
//
//            Intent IntentPortTwo = new Intent();
//            IntentPortTwo.setClass(ServiceMainActivity.this, MQTTTwoPortService.class);
//
//
//            if(msg != null) {
//                if (msg.equals("start_one")) {
//                    Bundle bundlePortOne = new Bundle();
//                    bundlePortOne.putSerializable("config_8882", defaultConfig("8882", confFile8882));
//                    IntentPortOne.putExtras(bundlePortOne);
//                    startService(IntentPortOne);
//                } else if (msg.equals("start_two")) {
//                    Bundle bundle2 = new Bundle();
//                    bundle2.putSerializable("config_8883", defaultConfig("8883", confFile8883));
//                    IntentPortTwo.putExtras(bundle2);
//                    startService(IntentPortTwo);
//                }else if (msg.equals("close_one")) {
//                    stopService(IntentPortOne);
//                }else if (msg.equals("close_two")) {
//                    stopService(IntentPortTwo);
//                }else if(msg.equals("1")) {
//                    Log.i("andysongTest=1==isrunning:", Utils.isServiceRunning(context.getApplicationContext(), MQTTService.class) +"");
//                }else if(msg.equals("2")) {
//                    Log.i("andysongTest=2==isrunning:", Utils.isServiceRunning(context.getApplicationContext(), MQTTPortOneService.class) +"");
//                }else if(msg.equals("3")) {
//                    Log.i("andysongTest=3==isrunning:", Utils.isServiceRunning(context.getApplicationContext(), MQTTTwoPortService.class) +"");
//                }
//            }
//        }
//    };

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 配置文件


//        startBtn = (Button) findViewById(R.id.start);
//        stopBtn = (Button) findViewById(R.id.stop);
//        bindBtn = (Button) findViewById(R.id.bind);
//        unBindBtn = (Button) findViewById(R.id.unbind);
//        startBtn.setOnClickListener(new MyOnClickListener());
//        stopBtn.setOnClickListener(new MyOnClickListener());
//        bindBtn.setOnClickListener(new MyOnClickListener());
//        unBindBtn.setOnClickListener(new MyOnClickListener());
//        intentFilter=new IntentFilter();
//        intentFilter.addAction("android.intent.action.BROADCAST_FORM_ADB"); //接收adb发送过来的广播包
////        registerReceiver(mReceiverBroadCast, intentFilter);         //注册广播
        startDefaultPort();
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
//        setVisible(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiverBroadCast);
    }

//    class MyOnClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//
//            if(v.getId() == R.id.start) {
//                // 启动Service
//                startService(intent);
//                toast("startService");
//            }else if (v.getId() == R.id.stop){
//                // 停止Service
//                stopService(intent);
//                toast("stopService");
//            }else if (v.getId() == R.id.bind){
//                // 绑定Service
//                bindService(intent, conn, Service.BIND_AUTO_CREATE);
//                toast("bindService");
//            }else if (v.getId() == R.id.unbind){
//                // 解除Service
//                unbindService(conn);
//                toast("unbindService");
//            }
//        }
//    }

    private void startDefaultPort(){
        Intent intent = new Intent();
        intent.setClass(ServiceMainActivity.this, MQTTService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("config",MyApp.defaultConfig("1883", MyApp.getConfFile()));
        intent.putExtras(bundle);
        startService(intent);
    }
//


    private void toast(final String tip){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private ServiceConnection conn = new ServiceConnection() {
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
    };
}