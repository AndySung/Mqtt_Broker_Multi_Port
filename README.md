# Mqtt_Broker_Multi_Port


## Introduce

##### An integrated MQTT Broker, message subscription server, Since the "'io.moquette:moquette-broker:0.11'" library does not currently support multi-port listening configuration on the Android side, I enabled three different port listening services here.





## Features

##### This interface and function are very simple, just open or close the Mqtt service

For this app, I removed the UI and just started the service in the background. I registered a broadcast in MQTTService. As the service started, I switched the other two ports (8882 and 8883) through the adb command. The default port number is 1883.



#### Broadcast

```java
intentFilter.addAction("android.intent.action.BROADCAST_FORM_ADB");
```



#### adb sends broadcast packet

- Open port number 8882

  ```shell
  andysong@andysong:~$ adb shell am broadcast -a android.intent.action.BROADCAST_FORM_ADB --es msg "open_port_8882"
  ```

- Close port number 8882

  ```shell
  andysong@andysong:~$ adb shell am broadcast -a android.intent.action.BROADCAST_FORM_ADB --es msg "stop_port_8882"
  ```

  

- Open port number 8883

  ```shell
  andysong@andysong:~$ adb shell am broadcast -a android.intent.action.BROADCAST_FORM_ADB --es msg "open_port_8883"
  ```

- Close port number 8883

  ```shell
  andysong@andysong:~$ adb shell am broadcast -a android.intent.action.BROADCAST_FORM_ADB --es msg "stop_port_8883"
  ```

  

- Port number 1883 is not closed by default





##### The left side of the picture below is to check whether the port number is occupied, and the right side is what it looks like when sending broadcast packets.

![image-20231214142212058](http://pic.song0123.com/img/image-20231214142212058.png)





#### Path to configuration file:

##### Requires Root permission

```shell
/data/user/0/com.soft.nice.mqttservice/app_media/ 
```



**acl_file.conf** :  <font color="blue">acl file to configure user and topic permissions</font>

 ![image-20231214143759871](http://pic.song0123.com/img/image-20231214143759871.png)

Format：

```shell
# This affects access control for clients with no username.
topic write SYS/test
topic read SYS/test
# This only affects clients with username "admin".
user admin
topic beijing
# This only affects clients with username "admin123".
user admin123
topic nanjing
# This only affects clients with username "admin345".
user admin345
topic shanghai
```



**pwd.conf**: <font color="blue">pwd file configures the username and password to log in to the broker</font>

 ![image-20231214144202403](http://pic.song0123.com/img/image-20231214144202403.png)

Format：

```shell
admin:8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
admin123:8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
admin345:8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
```

The username and password are separated by a colon, and the password is encrypted with "SHA-256"





**mqtt.properties**  

**mqtt_port_8882.properties**  

**mqtt_port_8883.properties**  

Except for the port, these configuration files are all the same.

Format：

```shell
#Thu Dec 14 06:33:24 GMT 2023
websocket_port=8080
port=1883
password_file=/data/user/0/com.soft.nice.mqttservice/app_media/pwd.conf
host=0.0.0.0
persistent_store=/storage/emulated/0/Android/data/com.soft.nice.mqttservice/files/moquette_store.mapdb
acl_file=/data/user/0/com.soft.nice.mqttservice/app_media/acl_file.conf
need_client_auth=true
allow_anonymous=false
```








