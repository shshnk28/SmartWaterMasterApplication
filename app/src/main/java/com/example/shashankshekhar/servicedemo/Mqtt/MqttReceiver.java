package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Logger.MqttLogger;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * Created by shashankshekhar on 09/11/15.
 * A message which the broker is broadcasting is received here.
 */
public class MqttReceiver implements MQTTConstants, MqttCallback {
    private static MqttReceiver mqttReceiver = null;

    private MqttAsyncClient mqttClient = null;
    private Context appContext = null;

    public MqttReceiver(Context context1) {
        mqttClient = SCMqttClient.getInstance();
        mqttClient.setCallback(this);
        appContext = context1;
    }

//    public synchronized static MqttReceiver getReceiverInstance(Context context1) {
//        if (mqttReceiver == null) {
//            mqttReceiver = new MqttReceiver(context1);
//            return mqttReceiver;
//        }
//        return mqttReceiver;
//    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) {
        /*
        do not perform any heavy operations here. ack will go back from here only after the method has
        finished running.keep it light
         */
//        CommonUtils.printLog("MQTT notif for topic: " + topic + "on thread " + CommonUtils.checkMainThread());
        // TODO: 10/11/15 call the library here that does the broadcast to seperate out the Mqtt implementation
//        Intent broadcast = new Intent();
//        broadcast.putExtra("message", msg.toString());
//        broadcast.putExtra("topicName", topic);
//        broadcast.setAction(topic);
//        appContext.sendBroadcast(broadcast);
        MqttLogger.writeDataToTempLogFile("message arr: " + msg.toString());
        CommonUtils.printLog("message arrived");
    }


    @Override
    public void connectionLost(Throwable cause) {
        CommonUtils.printLog("connection lost to broker");
        CommonUtils.printLog("cause: " + cause.getCause());
        String logString = "Connection lost to broker/ ";
        if (cause.getCause() != null) {
            logString += cause.getCause();
        }
        if (cause.getMessage()!= null) {
            logString+=cause.getMessage();
        }
        MqttLogger.initAppContext(appContext);
        MqttLogger.writeDataToLogFile(logString);
        //write to log file here as well
        MqttLogger.writeDataToTempLogFile(logString);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken tk) {
        CommonUtils.printLog("delivery complete");
        MqttLogger.initAppContext(appContext);
        MqttLogger.writeDataToTempLogFile("delivery complete");
    }
}
