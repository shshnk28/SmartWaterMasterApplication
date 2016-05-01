package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.DBOperations.SCDBOperations;
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
    public static String subscribeToTopic (final Context appContext,final String topicName, final Runnable onSuccess,
                                           final Runnable
            onFailure) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        if (mqttClient == null) {
            onFailure.run();
            return null;
        }
        try {
            mqttClient.subscribe(topicName, SUBS_QOS, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    CommonUtils.printLog("topic subscribed: " + topicName);
                    // add the entry to the db here
                    SCDBOperations.initDBAppContext(appContext);
                    SCDBOperations.addSubscribedTopicToDB(topicName);
                    onSuccess.run();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    CommonUtils.printLog("failed to subscribe to topic");
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
    public static void unsubscribeToTopic (final Context appContext, final String topicName, final Runnable onSuccess,
                                           final Runnable
            onFail
    ) {
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        if (mqttClient == null) {
            onFail.run();
            return;
        }
        try {
            mqttClient.unsubscribe(topicName, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    SCDBOperations.initDBAppContext(appContext);
                    SCDBOperations.removeUnsubscribedTopicFromDB(topicName);
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
