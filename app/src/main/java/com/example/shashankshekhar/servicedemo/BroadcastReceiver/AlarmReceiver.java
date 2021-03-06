package com.example.shashankshekhar.servicedemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FirstService;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.example.shashankshekhar.smartcampuslib.IncomingHandler;
import com.example.shashankshekhar.smartcampuslib.Interfaces.ServiceCallback;
import com.example.shashankshekhar.smartcampuslib.ServiceAdapter;
import com.example.shashankshekhar.smartcampuslib.SmartXLibConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by shashankshekhar on 08/04/16.
 */
public class AlarmReceiver extends BroadcastReceiver implements MQTTConstants, ServiceCallback,SmartXLibConstants {
    private static final String TEST_TOPIC = "iisc/smartx/crowd/network/ping";
    private static final String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    private static final String SMART_CAMPUS_LOG_FILE_NAME2 = "FromAlarm.txt";
    private static File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);

    Messenger clientMessenger;
    Message messageToPublish;
    Bundle bundleToPublish;
    Context appContext;
    ServiceAdapter serviceAdapter;
    public void onReceive(Context context, Intent intent) {
        clientMessenger = new Messenger(new IncomingHandler(context, this));
        appContext = context;
        serviceAdapter = new ServiceAdapter(context);
        checkConnection();
    }

//    private void configureMessage() {
//        messageToPublish = Message.obtain(null, PUBLISH_MESSAGE);
//        bundleToPublish = new Bundle();
//        bundleToPublish.putString("topicName", TEST_TOPIC);
//        messageToPublish.setData(bundleToPublish);
//        messageToPublish.replyTo = clientMessenger;
//    }

    private void publishMessage(String message) {
        serviceAdapter.publishGlobal(TEST_TOPIC, null, message, clientMessenger);
//        bundleToPublish.remove("dataString");
//        bundleToPublish.putString("dataString", message);
//        try {
//            SCServiceConnector.messenger.send(messageToPublish);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            CommonUtils.printLog("remote Exception,Could not send message");
//        }
    }

    private void checkConnection() {
        if (serviceAdapter.serviceConnected() == false) {
            CommonUtils.printLog("service not connected .. returning");
            return;
        }
        serviceAdapter.checkMqttConnection(clientMessenger);
//        Message message = Message.obtain(null, CHECK_MQTT_CONNECTION);
//        message.replyTo = clientMessenger;
//        try {
//            SCServiceConnector.messenger.send(message);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            CommonUtils.printLog("remote Exception,Could not send message");
//        }
    }


    @Override
    public void messageReceivedFromService(int status) {
        switch (status) {
            case MQTT_CONNECTION_IN_PROGRESS:
                /*
                return back. Do not do anything. This case should be rare
                 */
                break;
            case MQTT_NOT_CONNECTED:
                /*
                trigger a reconnect call
                 */
                CommonUtils.printLog("callback in alarm receiver- mqtt not connected");
                writeDataToLogFile("mqtt not connected in alarm receiver");
                if (CommonUtils.isNetworkAvailable(appContext) == false) {
                    CommonUtils.printLog("can not trigger reconnection in alarm receiver- no network");
                    break;
                } else {
                    Intent serviceIntent = new Intent(appContext, FirstService.class);
                    serviceIntent.putExtra("fromReconnecter", true);
                    appContext.startService(serviceIntent);
                    break;
                }

            case MQTT_CONNECTED:
                /*
                trigger a ping
                 */
                writeDataToLogFile("sending ping message");
                CommonUtils.printLog("callback in alarm receiver- mqtt connected,Sending ping");
                /*
                a publish to maintain connection
                 */
                publishMessage("ab");

                break;
        }
    }

    public void writeDataToLogFile(String logString) {
        String userName = readFromSharedPrefs(USER_NAME_KEY);
        String dateString = CommonUtils.getCurrentDate();
        String loggerString = dateString + "," + userName + "," + logString;
        if (smartCampusDirectory.exists() == false) {
            if (!smartCampusDirectory.mkdirs()) {
                CommonUtils.printLog("failed to create logfile ");
                return;
            }
        }
        File logFile = new File(smartCampusDirectory, SMART_CAMPUS_LOG_FILE_NAME2);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(loggerString);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            CommonUtils.printLog("file write failed in mqtt logfile");
            return;
        }
    }
    private  String readFromSharedPrefs (String key) {
        SharedPreferences settings = appContext.getSharedPreferences(PREFS_NAME,0);
        String userName = settings.getString(key, "Anon");
        if (userName.equals("Anon"))  {
            CommonUtils.printLog("shared prefs is null, returning Anon");
        }
        return userName;
    }
}

