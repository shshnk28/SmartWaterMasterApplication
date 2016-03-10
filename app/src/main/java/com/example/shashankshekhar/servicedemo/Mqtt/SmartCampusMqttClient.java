package com.example.shashankshekhar.servicedemo.Mqtt;

import android.util.Log;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by shashankshekhar on 09/11/15.
 */
// TODO: 12/11/15 you need to make it singleton. This class should have different types of constructors
// and each constructor should return a single object.


//public class SmartCampusMqttClient extends MqttClient implements MQTTConstants{
public class SmartCampusMqttClient  implements MQTTConstants{
    private static MqttClient SCMqttClient;
    private static MqttAsyncClient AsyncMqttClient;
    private static boolean initialiseMqttClient (Boolean persistenceConnection) {
        MemoryPersistence persistence = null;
        if (persistenceConnection == true) {
            persistence = new MemoryPersistence();
        }
        try {
            /*
            do not generate a random client id. if you reconnect withe the same client id then you do not have to
            resubscribe the topics.
            read here for more details. first para
            also in the connection options set the clean session to false
            http://www.hivemq.com/blog/mqtt-essentials-part-7-persistent-session-queuing-messages
             */
            // TODO: 29/02/16 remove random client id generation. it should be generated one time when the app installs
            String randomClientId = CommonUtils.randomString();
            SCMqttClient = new MqttClient(BROKER_ADDRESS_CLOUD,randomClientId ,persistence);
        } catch (MqttException ex) {
            ex.printStackTrace();
            CommonUtils.printLog("message : " + ex.getMessage());
            CommonUtils.printLog("reason : " + ex.getReasonCode());
            CommonUtils.printLog("cause : " + ex.getCause());
            CommonUtils.printLog("could not instantiate Mqttclient");
            SCMqttClient = null;
            return false;
        }
        return true;
    }
    public static synchronized MqttClient getMqttClient (Boolean persistence) {
        if (SCMqttClient == null) {
            CommonUtils.printLog("mqtt client null, initialisng in SmartCampusMqttClient");
            Boolean isInitialised = initialiseMqttClient(persistence);
            if (isInitialised) {
                return SCMqttClient;
            }
            return null;
        }
        return SCMqttClient;
}
    public static boolean isClientConnected () {
        MqttClient mqttClient = getMqttClient(true);
        return (mqttClient.isConnected());
    }
    /*
    code to make it singleton
     */
//    private static  SmartCampusMqttClient singleInstance = new SmartCampusMqttClient() ;
//    private SmartCampusMqttClient () throws SCException {
//        MemoryPersistence persistence = new MemoryPersistence();
//
//        try {
//
//            super(BROKER_ADDRESS,CLIENT_ID,new MemoryPersistence());
//            singleInstance = new MqttClient(BROKER_ADDRESS,CLIENT_ID,persistence);
//        } catch (MqttException ex) {
//            ex.printStackTrace();
//            CommonUtils.printLog("message : " + ex.getMessage());
//            CommonUtils.printLog("reason : " + ex.getReasonCode());
//            CommonUtils.printLog("cause : " + ex.getCause());
//            CommonUtils.printLog("could not instantiate Mqttclient");
//            singleInstance = null;
//        }
//    }
//
//    public synchronized static MqttClient getInstance () {
//        if (singleInstance == null) {
//            new SmartCampusMqttClient();
//        }
//        return singleInstance;
//    }

}
