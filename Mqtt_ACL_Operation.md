# <div align='center' ><font size='70'>Mqtt操作手册</font></div>



#### 由于文件存放到data/data/包名下面读取有问题（需要root），现在把配置文件存到了<font color = red>“/mnt/sdcard/Documents/mqtt/”</font> 路径下了，这样方便我们更改配置文件。

 ![image-20240110104048885](http://pic.song0123.com/img/image-20240110104048885.png)



#### 如果我们需要修改topic类型，只需要修改 <font color='red'>acl_file.conf</font>这个文件即可



文件格式：

```shell
# This affects access control for clients with no username.
topic write SYS/test
topic read SYS/test
# This only affects clients with username "admin".
user admin
topic beijin
# This only affects clients with username "admin123".
user admin123
topic nanjin
# This only affects clients with username "admin345".
user admin345
topic shanghai

```



例如：我们现在需要将用户名为<font color='blue'> admin</font> 的用户的topic更改为 支持所有topic, 需要把beijin更改为<font color='blue'>“$/#”</font>

```shell
# This affects access control for clients with no username.
topic write SYS/test
topic read SYS/test
# This only affects clients with username "admin".
user admin
topic $/#
# This only affects clients with username "admin123".
user admin123
topic nanjin
# This only affects clients with username "admin345".
user admin345
topic shanghai
```

经过测试发现，直接用#号不行，需要加转义字符$，应该是“io.moquette:moquette-broker:0.11” 的 ACL 文件格式导致的



可以将acl文件放在本地电脑，修改完成后，通过命令：push 到指定位置，例如：“adb push '/home/andysong/Desktop/acl_file.conf' /mnt/sdcard/Documents/mqtt/”





### 注意：修改完成后，需要替换掉<font color = red>“/mnt/sdcard/Documents/mqtt/”</font> 路径下的<font color='red'>acl_file.conf</font>文件，然后关闭Mqtt服务，重新打开。并且要重新连接mqtt服务才能生效。





### 查看服务是否开启

#### 通过adb命令查看服务状态

- ```shell
  adb shell
  netstat -anp | grep 1883
  
  这样就是查看端口号1883的服务是否开启了，如果显示“tcp6       0      0 [::]:1883               [::]:*                  LISTEN ”代表启动中，如果什么都不打印，则没有被开启
  ```

   ![image-20240110115623019](http://pic.song0123.com/img/image-20240110115623019.png)



- #### 通过Mqtt_broker App的通知查看

  需要将Mqtt_broker App通知权限打开

   <img src="http://pic.song0123.com/img/image-20240110134210532.png" alt="image-20240110134210532" style="zoom:67%;" />

 <img src="http://pic.song0123.com/img/image-20240110134246095.png" alt="image-20240110134246095" style="zoom:67%;" />

打开启动app后，会在通知栏上看到服务状态

 <img src="http://pic.song0123.com/img/image-20240110134356314.png" alt="image-20240110134356314" style="zoom:67%;" />







### 关闭服务的方式

#### 1. 通过命令行关闭

​	通过命令：adb shell am force-stop com.soft.nice.mqttservice，可以快速关闭服务



#### 2. 通过设备开发者模式关闭

 <img src="http://pic.song0123.com/img/image-20240110105342580.png" alt="image-20240110105342580" style="zoom:67%;" />

- 打开Developer Options



 <img src="http://pic.song0123.com/img/image-20240110105514237.png" alt="image-20240110105514237" style="zoom:67%;" />

- 查看Running services



 <img src="http://pic.song0123.com/img/image-20240110105601136.png" alt="image-20240110105601136" style="zoom:67%;" />

- 找到Mqtt_broker



 <img src="http://pic.song0123.com/img/image-20240110105644116.png" alt="image-20240110105644116" style="zoom:67%;" />

- 将其stop掉，并且清掉后台 Recent Tasks





### 重新启动Mqtt_broker App，并通过Mqtt Client 重新连接即可

