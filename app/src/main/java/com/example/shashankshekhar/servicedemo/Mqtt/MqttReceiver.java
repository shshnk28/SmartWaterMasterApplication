package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Logger.MqttLogger;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

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

    private MqttClient mqttClient = null;
    private MqttAsyncClient mqttAsyncClient;
    private MqttConnectOptions  connectionOptions = null;
    private Context appContext = null;

    private MqttReceiver (Context context1) {
        this.mqttClient = SmartCampusMqttClient.getMqttClient(true);
        this.connectionOptions= SCMqttConnectionOptions.getConnectionOptions();
        this.appContext = context1;
    }
    public synchronized static MqttReceiver getReceiverInstance (Context context1) {
        if (mqttReceiver == null) {
            mqttReceiver = new MqttReceiver(context1);
            return mqttReceiver;
        }
        return mqttReceiver;
    }
    public boolean initialiseReceiver () {
        boolean connection = connectToClientandSetcallback();
        return connection;
    }

    @Override
    public void messageArrived(String topic,MqttMessage msg)
    {
        /*
        do not perform any heavy operations here. ack will go back from here only after the method has
        finished running.keep it light
         */
        CommonUtils.printLog("MQTT notif for topic: " + topic+ "on thread "+ CommonUtils.checkMainThread());
         // TODO: 10/11/15 call the library here that does the broadcast to seperate out the Mqtt implementation
        Intent broadcast = new Intent();
        broadcast.putExtra("message",msg.toString());
        broadcast.putExtra("topicName",topic);
        broadcast.setAction(topic);
        appContext.sendBroadcast(broadcast);
    }


    @Override
    public void connectionLost(Throwable cause)
    {
        CommonUtils.printLog("connection lost to broker");
        CommonUtils.printLog("cause: "+ cause.getCause());
        String logString = "Connection lost to broker/ ";
        if (cause.getCause()!= null) {
            logString+=cause.getCause();
        }
        MqttLogger.initAppContext(appContext);
        MqttLogger.writeDataToLogFile(logString);
        //write to log file here as well
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken tk)
    {
        CommonUtils.printLog("delivery complete");
    }

    private boolean connectToClientandSetcallback () {
        /* we can define an error class and return the error object from this method with all the necessary info.*/
        mqttClient.setCallback(this);
        IMqttToken token = null;
        if (mqttClient == null || connectionOptions == null) {
            CommonUtils.printLog("client null in receiver ... returning ");
            return false;
        }
        if (mqttClient.isConnected() == true) {
            CommonUtils.printLog("already connected.. returning");
            return true;
        }
        try {
           token = mqttClient.connectWithResult(connectionOptions);

//            CommonUtils.printLog(token.);
        } catch (MqttSecurityException e) {
            CommonUtils.printLog("MqttSecurityException could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            String logStr = "MqttSecurityException/";
            if (e.getCause()!= null) {
                logStr+=" "+e.getCause()+"/";
            }
//            ConnectivityCheck connectivityCheck = new ConnectivityCheck(appContext);
//            connectivityCheck.checkNonConnectivityReason(logStr);
            return false;
        }
        catch (MqttException e) {
            CommonUtils.printLog(" non-security exception could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            String logStr = "MqttException/";
            if (e.getCause()!= null) {
                logStr+=" "+e.getCause()+"/";
            }
//            ConnectivityCheck connectivityCheck = new ConnectivityCheck(appContext);
//            connectivityCheck.checkNonConnectivityReason(logStr);
            return false;
        }
        CommonUtils.printLog("connection established with client: " + mqttClient.toString());
        MqttLogger.initAppContext(appContext);
        MqttLogger.writeDataToLogFile(" Connection Successful/");
        MqttLogger.runStatusPublisher(30);
        return token.isComplete();
    }
    public void disconnectMqtt () {
            if (mqttClient.isConnected() == false) {
                CommonUtils.printLog("not connected");
                return;
            }
        try {
            mqttClient.disconnect();
            CommonUtils.printLog("successfully disconnected");
        } catch (MqttException e) {
            CommonUtils.printLog("Error.Could not disconnect mqtt. Trying force");
            try {
                mqttClient.disconnectForcibly();
                CommonUtils.printLog("disconnection successful with force");
            } catch (MqttException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
