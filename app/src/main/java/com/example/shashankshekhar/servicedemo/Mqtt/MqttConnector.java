package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;

import com.example.shashankshekhar.servicedemo.Logger.MqttLogger;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * Created by shashankshekhar on 22/03/16.
 */
public class MqttConnector {
    public static boolean isConnecting;
    public static void connectToMqttClient(final Runnable onSuccess, final Runnable onFailure, final Context appContext) {

        /* we can define an error class and return the error object from this method with all the necessary info.*/
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
        MqttConnectOptions connectionOptions = SCMqttConnectionOptions.getConnectionOptions();
        IMqttToken token = null;
        if (mqttClient == null || connectionOptions == null) {
            CommonUtils.printLog("client null in receiver ... returning ");
            onFailure.run();
            return;
        }
        if (isConnecting == true) {
            CommonUtils.printLog("connection in progress in in mqttConnector .. returning");
            return;
        }
        try {
            isConnecting = true;
            token = mqttClient.connect(connectionOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    isConnecting = false;
                    CommonUtils.printLog("connection established with client: ");
                    MqttLogger.initAppContext(appContext);
                    MqttLogger.writeDataToLogFile(" Connection Successful/");
                    MqttLogger.runStatusPublisher(30);
                    onSuccess.run();
                    return;
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    isConnecting = false;
                    CommonUtils.printLog("failed to connect in failure block");
                    String logStr = "Failed to connect/";
                    onFailure.run();
                    if (throwable.getCause() != null) {
                        logStr+= " "+throwable.getCause() + "/";
                    }
                    ConnectivityCheck connectivityCheck  = new ConnectivityCheck(appContext);
                    connectivityCheck.checkNonConnectivityReason(logStr);
                    return;
                }
            });

//            CommonUtils.printLog(token.);
        } catch (MqttSecurityException e) {
            isConnecting = false;
            CommonUtils.printLog("MqttSecurityException could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            String logStr = "MqttSecurityException/";
            if (e.getCause() != null) {
                logStr += " " + e.getCause() + "/";
            }
            ConnectivityCheck connectivityCheck = new ConnectivityCheck(appContext);
            connectivityCheck.checkNonConnectivityReason(logStr);
        } catch (MqttException e) {
            isConnecting = false;
            CommonUtils.printLog(" non-security exception could not connect in receiver");
            CommonUtils.printLog("cause: " + e.getCause());
            String logStr = "MqttException/";
            if (e.getCause() != null) {
                logStr += " " + e.getCause() + "/";
            }
            ConnectivityCheck connectivityCheck = new ConnectivityCheck(appContext);
            connectivityCheck.checkNonConnectivityReason(logStr);
            onFailure.run();
        }

    }
    public static void disconnectMqtt () {
        if (SCMqttClient.isMqttConnected() == false) {
            CommonUtils.printLog("not connected");
            return;
        }
        MqttAsyncClient mqttClient = SCMqttClient.getInstance();
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
