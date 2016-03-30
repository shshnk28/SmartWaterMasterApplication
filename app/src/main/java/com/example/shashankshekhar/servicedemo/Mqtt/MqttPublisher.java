package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    public MqttPublisher (String topicName,String dataString) {
        this(topicName,null,dataString);
    }
    public MqttPublisher (String topicName) {
        this(topicName,null,null);
    }
    public void changePublisherData (String topicName,String eventName,String dataString) {
        if (topicName!=null && topicName.isEmpty() == false) {
            this.topicName = topicName;
        }
        if (eventName !=null && eventName.isEmpty() == false) {
            this.eventName = eventName;
        }
        if (dataString !=null && dataString.isEmpty() == false) {
            this.dataString = dataString;
        }
    }
    public boolean publishData(String data) {
        CommonUtils.printLog("trying to publish the topic");

        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        if (mqttClient == null) {
            CommonUtils.printLog("couldnot instantiate mqtt client..returning");
            return false;
        }
        String payload;
        if (eventName!=null) {
            payload = eventName + "-" + data;
        }
        else {
            payload = data;
        }
        MqttMessage message1 = new MqttMessage(payload.getBytes());
        message1.setQos(QoS);
        if (mqttClient.isConnected() == false) {
            CommonUtils.printLog("not connected to mqtt.. returning in publisher");
            return false;
        }
        try {
            // TODO: 16/02/16 the message has to be in json format. It needs to be made a standard in SmartX
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
    public boolean publishData() {
        if (dataString!=null) {
            boolean retVal = publishData(dataString);
            return retVal;
        }
        CommonUtils.printLog("data to be sent is null.. returning");
        return false;
    }

}

