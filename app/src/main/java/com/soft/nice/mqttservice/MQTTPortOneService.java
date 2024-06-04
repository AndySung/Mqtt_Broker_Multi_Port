package com.soft.nice.mqttservice;

import android.annotation.SuppressLint;
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

import java.util.Objects;

import io.moquette.server.Server;

/**
 * @author AndySong
 */
public class MQTTPortOneService extends Service {
    private static final String TAG = "NiceCIC>>>>>>>>MQTTPortOneService";
    public static final String CHANNEL_ID = "mqttBrokerChannel_8882";
    private final IBinder myBinder = new LocalBinder();
    PendingIntent pendingIntent;
    Server server;
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return myBinder;
    }

    /** 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法 **/
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
    }

    @SuppressLint({"ForegroundServiceType", "ObsoleteSdkInt"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStart()");
        Intent notificationIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        if(intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
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
            server.startServer(MyApp.defaultConfig("8882", MyApp.getConfFile8882()));
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        startForeground(101, startNotification());
        return START_STICKY;
    }


    /** Service创建并启动后在调用stopService方法或unbindService方法时调用该方法 **/
    @Override
    public void onDestroy() {
        server.stopServer();
        Log.e(TAG, "onDestroy()");
    }
    /** 提供给客户端访问 **/
    public class LocalBinder extends Binder {
        MQTTPortOneService getService() {
            return MQTTPortOneService.this;
        }
    }

    private Notification startNotification(){
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notification Broker",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText("MQTT Service running...")
                .setContentTitle("MQTT Service port 8882")
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_robot);
        notification.setContentIntent(pendingIntent);
        return notification.build();
    }
}
