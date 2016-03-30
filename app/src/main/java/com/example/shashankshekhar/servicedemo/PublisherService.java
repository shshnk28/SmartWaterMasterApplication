package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.shashankshekhar.servicedemo.Logger.MqttLogger;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttConnector;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.Mqtt.SCMqttClient;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PublisherService extends Service {
    private final String TEST_TOPIC = "iisc/smartx/crowd/network/mqttTest";
    String userName;

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
    public void onCreate() {
        while (true) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (SCMqttClient.isMqttConnected() == true) {
                        String data = getCurrentDate() + "," + userName;
                        CommonUtils.printLog(data);
//                        MqttPublisher publisher = new MqttPublisher(TEST_TOPIC);
//                        publisher.publishData(data);
                    } else {
                        CommonUtils.printLog("Mqtt is not connected in publisher thread");
                        MqttLogger.initAppContext(getApplicationContext());
                        MqttLogger.writeDataToLogFile("Mqtt not connected");
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }
}
