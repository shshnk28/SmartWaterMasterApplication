package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by shashankshekhar on 22/03/16.
 */
public class SCMqttClient extends MqttAsyncClient implements MQTTConstants {
    private static SCMqttClient scMqttClient;
    private SCMqttClient(String brokerAddress,String clientId,MqttClientPersistence persistence) throws MqttException{
        super(brokerAddress,clientId,persistence);
    }
     public static SCMqttClient getInstance() {
         if (scMqttClient !=null){
             return scMqttClient;
         }
         try {
             scMqttClient = new SCMqttClient(BROKER_ADDRESS_CLOUD, CommonUtils.randomString(),new MemoryPersistence());
             return scMqttClient;
         } catch (MqttException e) {
             CommonUtils.printLog("message : " + e.getMessage());
             CommonUtils.printLog("reason : " + e.getReasonCode());
             CommonUtils.printLog("cause : " + e.getCause());
             CommonUtils.printLog("could not instantiate Mqttclient");
         }
         return null;
     }
    public static boolean isMqttConnected() {
        if (scMqttClient == null) {
            return false;
        }
        return scMqttClient.isConnected();
    }
}