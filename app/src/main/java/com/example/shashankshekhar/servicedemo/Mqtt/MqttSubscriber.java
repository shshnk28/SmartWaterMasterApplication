package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by shashankshekhar on 12/11/15.
 */
public class MqttSubscriber implements MQTTConstants {
    public static String subscribeToTopic (String topicName, final Runnable onSuccess, final Runnable onFailure) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        try {
            mqttClient.subscribe(topicName, QoS, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    CommonUtils.printLog("topic subscribed: " + iMqttToken.toString());
                    onSuccess.run();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    onFailure.run();
                }
            });

        } catch (MqttException e) {
            CommonUtils.printLog("failed to subscribe in mqttsubscriber");
            e.printStackTrace();
            return null;
        }
        return CommonUtils.randomString();
    }
    public static void unsubscribeToTopic (String topicName, final Runnable onSuccess, final Runnable onFail ) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        try {
            mqttClient.unsubscribe(topicName, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    onSuccess.run();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    onFail.run();
                }
            });
        } catch (MqttException e) {
            CommonUtils.printLog("failed to unsubscribe in mqttsubscriber");
            e.printStackTrace();
        }
    }
}
