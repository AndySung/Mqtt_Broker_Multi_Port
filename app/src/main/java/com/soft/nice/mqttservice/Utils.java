package com.soft.nice.mqttservice;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {
    private static final String TAG = "NiceCIC>>>>>>>>Utils";
    public static final String PASSWORD_FILE = "pwd.conf";
    public static final String BROKER_CONFIG_FILE = "mqtt.properties";
    public static final String BROKER_CONFIG_PORT8882_FILE = "mqtt_port_8882.properties";
    public static final String BROKER_CONFIG_PORT8883_FILE = "mqtt_port_8883.properties";
    public static final String ACL_FILE = "acl_file.conf";

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> serviceList = manager.getRunningServices(Integer.MAX_VALUE);
            Log.i(TAG, serviceList.size()+"\n");
            if (serviceList != null) {
                for (ActivityManager.RunningServiceInfo serviceInfo : serviceList) {
                    Log.i(TAG, serviceInfo.service.getClassName()+"\n");
                    if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String  getVersion() {
        String javaVersion = System.getProperty("java.version");
        System.out.format("Java Version = '%s'", javaVersion);
        return javaVersion;
    }

    public static String getBrokerURL(Context ctx) {
        return Formatter.formatIpAddress(((WifiManager) ctx.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress());
    }

    public static void showDialog(Context content, String message) {
        new AlertDialog.Builder(content).setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }
}

