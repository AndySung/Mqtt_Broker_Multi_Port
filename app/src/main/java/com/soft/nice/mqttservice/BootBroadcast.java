package com.soft.nice.mqttservice;

import static com.soft.nice.mqttservice.Utils.isServiceRunning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonUiContext;

import java.util.Objects;

/**
 * @author AndySong
 */
public class BootBroadcast extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "NiceCIC>>>>>>>>BootBroadcast";
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG,"Started at boot");
        if (Objects.equals(intent.getAction(), ACTION)){
            Log.d(TAG,"Started up and received the boot broadcast packet");
            Intent newIntent = context.getPackageManager().getLaunchIntentForPackage("com.soft.nice.mqttservice");
            //Intent newIntent = new Intent(context, ServiceMainActivity.class);
            if (newIntent != null) {
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(newIntent);

            Log.d(TAG, "onReceive BOOT_COMPLETED!! ");
            //启动MQTTService服务
            Intent bootServiceIntent = new Intent(context, MQTTService.class);
            if (!isServiceRunning(context, MQTTService.class)) {
                context.startForegroundService(bootServiceIntent);
                Log.d(TAG, "mqtt service has started");
            }
        }
    }
}