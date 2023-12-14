package com.soft.nice.mqttservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.soft.nice.testservice.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.moquette.server.Server;

public class MQTTPortOneService extends Service {
    private static final String TAG = "MyService";
    public static final String CHANNEL_ID = "mqttBrokerChannel_8882";
    private final IBinder myBinder = new LocalBinder();
    PendingIntent pendingIntent;
    Server server;
    Properties config = new Properties();
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return myBinder;
    }

    // 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStart()");
        if(intent.getSerializableExtra("config_8882") != null){
            Map<String, String> map = (HashMap) intent.getSerializableExtra("config_8882");
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
        Log.e(TAG, "onDestroy()");
    }
    //提供给客户端访问
    public class LocalBinder extends Binder {
        MQTTPortOneService getService() {
            return MQTTPortOneService.this;
        }
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
                .setContentTitle("MQTT Service port 8882")
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentIntent(pendingIntent);
        return notification.build();
    }
}
