package com.example.shashankshekhar.servicedemo.Mqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.IntegerRes;

import com.example.shashankshekhar.servicedemo.BroadcastReceiver.AlarmReceiver;
import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.FileHandler.MqttLogger;
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
public class MqttConnector implements MQTTConstants {
    public static boolean isConnecting;
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;
    public static void connectToMqttClient(final Runnable onSuccess, final Runnable onFailure, final Context appContext) {

        /* we can define an error class and return the error object from this method with all the necessary info.*/
        /*
        reset the client so that we can get a client with new properties
         */
        SCMqttClient.resetMqttClient();
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
//                    MqttLogger.runStatusPublisher(30);
                    setPingAlarm(appContext);
                    onSuccess.run();
                    return;
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    isConnecting = false;
                    String logStr = "Failed to connect/";
                    onFailure.run();
                    if (throwable.getCause() != null) {
                        logStr+= " "+throwable.getCause() + "/";
                    }
                    ConnectivityCheck connectivityCheck  = new ConnectivityCheck(appContext);
                    connectivityCheck.checkNonConnectivityReason(logStr);
                    CommonUtils.printLog("failed to connect in failure block" + logStr);
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
    private static void setPingAlarm (Context appContext) {
        /*
        chck if the mqtt is connected if not then trigger a connection req else send a ping
         */
        CommonUtils.printLog(" process id when setting alarm: " + android.os.Process.myPid());
        alarmManager = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(appContext, 0, intent, 0);
        CommonUtils.printLog("reading form json");
        int pingFreq = Integer.parseInt(ConnOptsJsonHandler.readFromJsonFile(PING_FREQ_KEY));
        pingFreq *=60 *1000; // converting the val from minutes to millisec
        CommonUtils.printLog("setting alarm: " + pingFreq);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, pingFreq, pingFreq,pendingIntent);
        MqttLogger.writeDataToTempLogFile("alarm set");

        // set up the wifi lock
//        WifiManager wm = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
//        WifiManager.WifiLock wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL , "MyWifiLock");
//        wifiLock.acquire();
    }
    public static void cancelAlarm () {
        CommonUtils.printLog("connection lost canceling alarm");
        alarmManager.cancel(pendingIntent);
    }

}
