package com.soft.nice.mqttservice;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

import io.moquette.BrokerConstants;

public class MyApp extends Application {
    private static final String TAG = "NiceCIC>>>>>>>>MyApp";
    public static MyApp instance;
    public static File confFile, confFile8882, confFile8883, passwordFile, aclFile;
    public static MqttProperties props;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        makeMqttFolder();
        //getApplicationContext().getDir("media", 0).getAbsolutePath()  目录：data/data/com.soft.xxx
        confFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +"/mqtt/"+ Utils.BROKER_CONFIG_FILE);
        confFile8882 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +"/mqtt/"+ Utils.BROKER_CONFIG_PORT8882_FILE);
        confFile8883 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +"/mqtt/"+ Utils.BROKER_CONFIG_PORT8883_FILE);
        passwordFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +"/mqtt/"+ Utils.PASSWORD_FILE);
        aclFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/mqtt/"+Utils.ACL_FILE);
        props = new MqttProperties();
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static Properties defaultConfig(String whichPort, File whichFile) {
        props.setProperty( BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME, getInstance().getExternalFilesDir(null).getAbsolutePath() + File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);
        //指定端口号
        props.setProperty(BrokerConstants.PORT_PROPERTY_NAME, whichPort);
        //必须设置用户名认证，不允许匿名连接
        props.setProperty(BrokerConstants.NEED_CLIENT_AUTH, "true");
        //这里直接设置成 0.0.0.0
        props.setProperty(BrokerConstants.HOST_PROPERTY_NAME, BrokerConstants.HOST);
        //websocket 端口号
        props.setProperty(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, String.valueOf(BrokerConstants.WEBSOCKET_PORT));
        //writeToAclFile(MyApp.getAclFile());                                                        //订阅topic规则文件
        //不允许匿名连接
        props.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, "false");
        //设定用户名密码
        //writeToPasswordFile(MyApp.getPasswordFile());
        //保存密码文件
        props.setProperty(BrokerConstants.PASSWORD_FILE_PROPERTY_NAME, MyApp.getPasswordFile().getAbsolutePath());
        //保存topic规则文件
        props.setProperty(BrokerConstants.ACL_FILE_PROPERTY_NAME, MyApp.getAclFile().getAbsolutePath());
        try (OutputStream output = Files.newOutputStream(whichFile.toPath())) {
            //添加文件更新时间
            props.store(output, "Last saved on " + new Date());
            return props;
        } catch (IOException io) {
            writeToAclFile(MyApp.getAclFile());
            writeToPasswordFile(MyApp.getPasswordFile());
            Log.i("MAIN", "Unable to load broker config file. Using default config");
            return defaultConfig("1883", MyApp.getConfFile());
        }
    }

    /** 填写ACL文件（测试数据）【Test ACL file availability】 **/
    public static void writeToAclFile(File aclFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(aclFile)) {
            fileOutputStream.write("# This affects access control for clients with no username.\n".getBytes());
            fileOutputStream.write("topic write SYS/test\n".getBytes());
            fileOutputStream.write("topic read SYS/test\n".getBytes());
            fileOutputStream.write("# This only affects clients with username \"admin\".\n".getBytes());
            fileOutputStream.write("user admin\n".getBytes());
            fileOutputStream.write("topic beijin\n".getBytes());
            fileOutputStream.write("# This only affects clients with username \"admin123\".\n".getBytes());
            fileOutputStream.write("user admin123\n".getBytes());
            fileOutputStream.write("topic nanjin\n".getBytes());
            fileOutputStream.write("# This only affects clients with username \"admin345\".\n".getBytes());
            fileOutputStream.write("user admin345\n".getBytes());
            fileOutputStream.write("topic shanghai\n".getBytes());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 填写密码文件（测试数据）【Test password file availability】 **/
    public static void writeToPasswordFile(File passwordFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(passwordFile)) {
            fileOutputStream.write("admin".getBytes());
            fileOutputStream.write(":".getBytes());
            fileOutputStream.write(Utils.getSHA("admin").getBytes());
            fileOutputStream.write("\n".getBytes());

            fileOutputStream.write("admin123".getBytes());
            fileOutputStream.write(":".getBytes());
            fileOutputStream.write(Utils.getSHA("admin").getBytes());
            fileOutputStream.write("\n".getBytes());

            fileOutputStream.write("admin345".getBytes());
            fileOutputStream.write(":".getBytes());
            fileOutputStream.write(Utils.getSHA("admin").getBytes());
            fileOutputStream.write("\n".getBytes());
            return;
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public static File getConfFile() {
        return confFile;
    }

    public static File getConfFile8882() {
        return confFile8882;
    }

    public static File getConfFile8883() {
        return confFile8883;
    }

    public static File getPasswordFile() {
        return passwordFile;
    }

    public static File getAclFile() {
        return aclFile;
    }

    public static void makeMqttFolder() {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/mqtt";
        File folder = new File(folderPath);
        if(!folder.exists()) {
            boolean success = folder.mkdirs();
            if(success) {
                Log.i(TAG, "Folder created successfully");
            }else{
                Log.i(TAG, "Folder creation failed");
            }
        }
    }
}
