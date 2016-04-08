package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by shashankshekhar on 12/11/15.
 */
public class MqttSubscriber implements MQTTConstants {
    public static String subscribeToTopic (String topicName) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        try {
            mqttClient.subscribe(topicName, QoS);
            CommonUtils.printLog("topic subscribed: " + topicName);
            CommonUtils.printLog("for client: "+ mqttClient.toString());

        } catch (MqttException e) {
            CommonUtils.printLog("failed to subscribe in mqttsubscriber");
            e.printStackTrace();
            return null;
        }
        return CommonUtils.randomString();
    }
    public static void unsubscribeToTopic (String topicName) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        try {
            mqttClient.unsubscribe(topicName);
        } catch (MqttException e) {
            CommonUtils.printLog("failed to unsubscribe in mqttsubscriber");
            e.printStackTrace();
        }
    }
}
