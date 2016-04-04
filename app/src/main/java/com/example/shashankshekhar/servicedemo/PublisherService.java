package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.*;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class PublisherService extends Service implements ServiceCallback,MQTTConstants {
    private final String TEST_TOPIC = "iisc/smartx/crowd/network/mqttTest";
    private final int SLEEP_TIME = 120*1000; // 120 secs
    private static final String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    private static final String SMART_CAMPUS_LOG_FILE_NAME2 = "FromPubThread.txt";
    private static File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);
    String userName;
    Messenger clientMessenger;
    Message messageToPublish;
    Bundle bundleToPublish;
    public PublisherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userName = intent.getStringExtra("userName");
        return START_NOT_STICKY;
    }
    @Override
    public void messageReceivedFromService(int number) {
        switch (number) {
            case TOPIC_PUBLISHED:
                CommonUtils.printLog("topic published");
                break;
            case ERROR_IN_PUBLISHING:
                CommonUtils.printLog("error in publshing");
                break;
            case NO_NETWORK_AVAILABLE:
                CommonUtils.printLog("network is not available");
                break;
            case MQTT_NOT_CONNECTED:
                CommonUtils.printLog("mqtt is not connected");
                break;
            default:
        }
    }

    @Override
    public void serviceConnected () {

    }
    @Override
    public void serviceDisconnected() {
//        CommonUtils.printLog("service disconnecetd");
    }

    @Override
    public void onCreate() {
        configureMessage();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                while (true) {
                    String dataString = getCurrentDate() + "," + userName;
                    publishMessage(dataString);
                    writeDataToLogFile("Publish message sent from Publisher thread");
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }
    private void configureMessage () {
        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(),this));
        messageToPublish = Message.obtain(null, PUBLISH_MESSAGE);
        bundleToPublish = new Bundle();
        bundleToPublish.putString("topicName", TEST_TOPIC);
        messageToPublish.setData(bundleToPublish);
        messageToPublish.replyTo = clientMessenger;
    }
    private void publishMessage (String message) {
        bundleToPublish.remove("dataString");
        bundleToPublish.putString("dataString", message);
        try {
            SCServiceConnector.messenger.send(messageToPublish);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void writeDataToLogFile(String logString) {
        String userName = "Shashank";
        String dateString = getCurrentDate();
        String loggerString = dateString + "," +userName + ","+ logString;
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
}
