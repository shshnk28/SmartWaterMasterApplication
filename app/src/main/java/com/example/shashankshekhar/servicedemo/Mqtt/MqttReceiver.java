package com.example.shashankshekhar.servicedemo.Mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FirstService;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.lang.ref.PhantomReference;
import java.util.ConcurrentModificationException;

/**
 * Created by shashankshekhar on 09/11/15.
 * A message which the broker is broadcasting is received here.
 */
public class MqttReceiver implements MQTTConstants, MqttCallback {
    private static MqttReceiver mqttReceiver = null;

    private MqttClient mqttClient = null;
    private MqttConnectOptions  connectionOptions = null;
    private Context context = null;

    private MqttReceiver (Context context1) {
        this.mqttClient = SmartCampusMqttClient.getMqttClient(true);
        this.connectionOptions= SCMqttConnectionOptions.getConnectionOptions();
        this.context = context1;
    }
    public synchronized static MqttReceiver getReceiverInstance (Context context1) {
        if (mqttReceiver == null) {
            mqttReceiver = new MqttReceiver(context1);
            return mqttReceiver;
        }
        return mqttReceiver;
    }
    public void initialiseReceiver () {
//        CommonUtils.printLog("receiver initialising");
        boolean connection = connectToClientandSetcallback();

//        if (connection == true && context!=null ) {
//            Intent broadcast = new Intent();
//            broadcast.setAction("iisc/smartx/water/data");
//            context.sendBroadcast(broadcast);
//        }
    }

    @Override
    public void messageArrived(String topic,MqttMessage msg)
    {
        CommonUtils.printLog("data received in MQTT Receiver for topic: " + topic);
         // TODO: 10/11/15 call the library here that does the broadcast to seperate out the Mqtt implementation
        Intent broadcast = new Intent();
        broadcast.putExtra("message",msg.toString());
        broadcast.setAction(topic);
        context.sendBroadcast(broadcast);
    }


    @Override
    public void connectionLost(Throwable cause)
    {
        CommonUtils.printLog("connection lost in receiver!!");
        CommonUtils.printLog("cause: "+ cause.getCause());
        CommonUtils.printLog("Message: "+ cause.getMessage());
        CommonUtils.printLog("LocalizedMessage: "+ cause.getLocalizedMessage());
//        ca.printStackTrace();
//        CommonUtils.printLog(ca.getCause().toString());
        boolean connection = connectToClientandSetcallback();
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken tk)
    {
//        try {
//            CommonUtils.printLog(tk.getMessage().toString()+ "in delivery complete");
//        } catch (MqttException ex) {
//            ex.printStackTrace();
//            CommonUtils.printLog("could notget ");
//        }

        CommonUtils.printLog("delivery complete");
    }

    private boolean connectToClientandSetcallback () {
        mqttClient.setCallback(this);
        if (mqttClient == null || connectionOptions == null) {
            CommonUtils.printLog("client null in receiver ... returning ");
            return false;
        }
        if (mqttClient.isConnected() == true) {
            CommonUtils.printLog("already connected.. returning");
            return true;
        }
        try
        {  CommonUtils.printLog("sending connection request");
           IMqttToken token = mqttClient.connectWithResult(connectionOptions);
            CommonUtils.printLog("connection response received" + token.isComplete());
//            CommonUtils.printLog(token.);
        } catch (MqttSecurityException e)
        {
            CommonUtils.printLog("MqttSecurityException could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            e.printStackTrace();
            return false;
        }
        catch (MqttException e)
        {
            CommonUtils.printLog(" non-security exception could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            CommonUtils.printLog("reason code: " + e.getReasonCode());
            e.printStackTrace();
            return false;
        }
        CommonUtils.printLog("connection established with client: " + mqttClient.toString());
        return true;

    }
}
