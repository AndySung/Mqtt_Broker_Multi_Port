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
import android.os.IBinder;
import android.util.Log;

import java.util.Objects;

import io.moquette.server.Server;

/**
 * @author AndySong
 */
public class MQTTService extends Service {
    private static final String TAG = "NiceCIC>>>>>>>>MQTTService";
    public static final String CHANNEL_ID = "mqttBrokerChannel_1883";
    private final IBinder myBinder = new LocalBinder();
    PendingIntent pendingIntent;
    IntentFilter intentFilter;
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
                if ("open_port_8882".equals(msg)) {
                    if(!Utils.isServiceRunning(context.getApplicationContext(), MQTTPortOneService.class)) {
                        startService(IntentPortOne);
                    }
                } else if ("open_port_8883".equals(msg)) {
                    if(!Utils.isServiceRunning(context.getApplicationContext(), MQTTTwoPortService.class)) {
                        startService(IntentPortTwo);
                    }
                }else if ("stop_port_8882".equals(msg)) {
                    stopService(IntentPortOne);
                }else if ("stop_port_8883".equals(msg)) {
                    stopService(IntentPortTwo);
                }else if("crash".equals(msg)) {
                    causeCrash();
                }
            }
        }
    };

    /** 提供给客户端访问 **/
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

    /** 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法 **/
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        // 配置文件
        intentFilter=new IntentFilter();
        //接收adb发送过来的广播包
        intentFilter.addAction("android.intent.action.BROADCAST_FORM_ADB");
        //注册广播
        registerReceiver(mReceiverBroadCast, intentFilter);
    }

    /** 调用startService方法启动Service时调用该方法 **/
    @SuppressLint({"ForegroundServiceType", "ObsoleteSdkInt"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand()");
        Intent notificationIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        if(intent!=null) {
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
            server.startServer(MyApp.defaultConfig("1883", MyApp.getConfFile()));
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        startForeground(1, startNotification());
        return Service.START_NOT_STICKY;    // START_STICKY = 1,服务挂掉后会尝试重新拉取保活，START_NOT_STICKY = 2，服务挂了不会拉取服务了

    }

    /** Service创建并启动后在调用stopService方法或unbindService方法时调用该方法 **/
    @Override
    public void onDestroy() {
        server.stopServer();
        unregisterReceiver(mReceiverBroadCast);
        Log.e(TAG, "onDestroy()");
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
                .setContentTitle("MQTT Service")
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.app_icon);
        notification.setContentIntent(pendingIntent);
        return notification.build();
    }

    //Crash Testing
    public void causeCrash() {
        String[] testArray = {"a", "b", "c"};
        // 故意访问一个不存在的索引，这里索引4是超出数组范围的
        String testValue = testArray[4];
        // 这段代码永远不会被执行，因为上面的代码会抛出异常
        Log.d("CRASH", "This will crash the app: " + testValue);
    }

}