package com.soft.nice.mqttservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcast extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "NiceCIC>>>>>>>>BootBroadcast";
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG,"Started at boot");
        if (intent.getAction().equals(ACTION)){
            Log.d(TAG,"Started up and received the boot broadcast packet");
            Intent newIntent = context.getPackageManager().getLaunchIntentForPackage("com.soft.nice.mqttservice");
            //Intent newIntent = new Intent(context, ServiceMainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            Intent bootServiceIntent = new Intent(context, MQTTService.class);
            Log.d(TAG, "onReceive BOOT_COMPLETED!! ");
            context.startService(bootServiceIntent);
            Log.d(TAG,"mqtt service has started");
        }
    }
}