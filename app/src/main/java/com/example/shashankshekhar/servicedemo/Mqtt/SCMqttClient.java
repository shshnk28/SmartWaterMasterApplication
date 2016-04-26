package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.FileHandler.MqttLogger;
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
//    private static ClientComms clientComms;
//    private static TimerPingSender pingSender;
    private SCMqttClient(String brokerAddress,String clientId,MqttClientPersistence persistence) throws MqttException{
        super(brokerAddress,clientId,persistence);
    }
     public static SCMqttClient getInstance() {
         if (scMqttClient !=null){
             return scMqttClient;
         }
         try {
             MemoryPersistence persistence = new MemoryPersistence();
             CommonUtils.printLog("Connecting with client id: " + CommonUtils.getClientId());
             String brokerAdd = ConnOptsJsonHandler.readFromJsonFile(BROKER_ADDRESS_KEY);
             String portNum = ConnOptsJsonHandler.readFromJsonFile(PORT_NUM_KEY);
             String serverUrl = TCP_BROKER_PREFIX + brokerAdd+":"+portNum;
             CommonUtils.printLog("serverURL: "+serverUrl);
             scMqttClient = new SCMqttClient(serverUrl, CommonUtils.getClientId(),
                     persistence);
//             pingSender = new TimerPingSender();
//             clientComms = new ClientComms(scMqttClient,persistence,pingSender);
//             pingSender.init(clientComms);
//             pingSender.start();
             MqttLogger.writeDataToTempLogFile("client connected");
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
 public static void resetMqttClient () {
     scMqttClient = null;
 }
}
