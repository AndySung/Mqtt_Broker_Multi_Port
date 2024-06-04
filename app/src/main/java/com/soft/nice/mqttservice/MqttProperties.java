package com.soft.nice.mqttservice;

import java.io.Serializable;
import java.util.Properties;

/**
 * @Author : AndySong
 * @Email : hiandysong@gmail.com
 * @Date : on 2024-05-31 15:21
 * &#064;Description  : 用于传输数据的，先留着
 * serialVersionUID: 是可选的,但建议添加以确保序列化和反序列化的兼容性
 */
public class MqttProperties extends Properties implements Serializable{
    private static final long serialVersionUID = 1L;
    private String extraField;

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }
}
