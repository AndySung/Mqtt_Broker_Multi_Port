package com.soft.nice.mqttservice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.moquette.server.Server;

public class MQTTService extends Service {
    private static final String TAG = "NiceCIC>>>>>>>>MQTTService";
    public static final String CHANNEL_ID = "mqttBrokerChannel_1883";
    private final IBinder myBinder = new LocalBinder();
    PendingIntent pendingIntent;
    IntentFilter intentFilter;
    Properties config = new Properties();
    Server server;

    private final BroadcastReceiver mReceiverBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            Intent IntentPortOne = new Intent();
            IntentPortOne.setClass(getApplicationContext(), MQTTPortOneService.class);

            Intent IntentPortTwo = new Intent();
            IntentPortTwo.setClass(getApplicationContext(), MQTTTwoPortService.class);

            if(msg != null) {
                if (msg.equals("open_port_8882")) {
                    Bundle bundlePortOne = new Bundle();
                    bundlePortOne.putSerializable("config_8882", MyApp.defaultConfig("8882", MyApp.getConfFile8882()));
                    IntentPortOne.putExtras(bundlePortOne);
                    if(!Utils.isServiceRunning(context.getApplicationContext(), MQTTPortOneService.class)) {
                        startService(IntentPortOne);
                    }
                } else if (msg.equals("open_port_8883")) {
                    Bundle bundlePortTwo = new Bundle();
                    bundlePortTwo.putSerializable("config_8883", MyApp.defaultConfig("8883", MyApp.getConfFile8883()));
                    IntentPortTwo.putExtras(bundlePortTwo);
                    if(!Utils.isServiceRunning(context.getApplicationContext(), MQTTTwoPortService.class)) {
                        startService(IntentPortTwo);
                    }
                }else if (msg.equals("stop_port_8882")) {
                    stopService(IntentPortOne);
                }else if (msg.equals("stop_port_8883")) {
                    stopService(IntentPortTwo);
                }
            }
        }
    };

    //提供给客户端访问
    public class LocalBinder extends Binder {
        MQTTService getService() {
            return MQTTService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return myBinder;
    }

    // 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        // 配置文件
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.BROADCAST_FORM_ADB"); //接收adb发送过来的广播包
        registerReceiver(mReceiverBroadCast, intentFilter);         //注册广播
    }

    // 调用startService方法启动Service时调用该方法
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStart()");
        Log.i(TAG, "message:"+intent.getSerializableExtra("config")+"");
        if(intent.getSerializableExtra("config") != null){
            Map<String, String> map = (HashMap) intent.getSerializableExtra("config");
            config.putAll(map);
        }
        Intent notificationIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flag = 0;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }else{
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flag);
        try {
            // use ServerInstance singleton to get the same instance of server
            server = new Server();
            server.startServer(config);
            Log.d(TAG, "MQTT Broker Started" + config.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        startForeground(1, StartNotification("MQTT Service running..."));
        return START_STICKY;

    }

    // Service创建并启动后在调用stopService方法或unbindService方法时调用该方法
    @Override
    public void onDestroy() {
        server.stopServer();
        unregisterReceiver(mReceiverBroadCast);
        Log.e(TAG, "onDestroy()");
    }

    private Notification StartNotification(String ContentId){
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notification Broker",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText(ContentId)
                .setContentTitle("MQTT Service")
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.app_icon);
        notification.setContentIntent(pendingIntent);
        return notification.build();
    }

}