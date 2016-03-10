package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;
import android.util.Log;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by shashankshekhar on 06/11/15.
 * handles the interaction with MQTT library for pub-sub events
 *
 */
public class MqttPublisher implements MQTTConstants {
    String topicName;
    String eventName;
    String dataString;
    public MqttPublisher (String topicName,String eventName,String dataString) {
        this.topicName = topicName;
        this.eventName = eventName;
        this.dataString= dataString;
    }
    public boolean publishTopic () {
        CommonUtils.printLog("trying to publish the topic");

        MqttClient mqttClient = SmartCampusMqttClient.getMqttClient(true);
        if (mqttClient == null) {
            CommonUtils.printLog("couldnot instantiate mqtt client..returning");
            return false;
        }
//        CommonUtils.printLog("client address in publisher: "+mqttClient.toString());
        String payload = eventName + "-" + dataString;
        MqttMessage message1 = new MqttMessage(payload.getBytes());
        message1.setQos(QoS);
        if (mqttClient.isConnected() == false) {
            CommonUtils.printLog("not connected to mqtt.. returning in publisher");
            return false;
        }
        try {
            // TODO: 16/02/16 the message has to be in json format. It needs to be made a standard in SmartX
            // TODO: 23/02/16 the publishing is on main thread. change this
            CommonUtils.printLog("main thread in publisher: " + CommonUtils.checkMainThread());
            mqttClient.publish(topicName, message1);
            CommonUtils.printLog("successfully published@" + topicName);
            return true;
        } catch (MqttException ex){
            ex.printStackTrace();
            CommonUtils.printLog("failed to publish");
            CommonUtils.printLog("message : " + ex.getMessage());
            CommonUtils.printLog("reason : " + ex.getReasonCode());
            CommonUtils.printLog("cause : " + ex.getCause());
            return false;
        }


    }

}

