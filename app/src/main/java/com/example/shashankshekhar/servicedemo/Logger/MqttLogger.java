package com.example.shashankshekhar.servicedemo.Logger;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;
import java.util.TimeZone;

/**
 * Created by shashankshekhar on 10/03/16.
 */
public class MqttLogger {
    private static final String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    private static final String SMART_CAMPUS_LOG_FILE_NAME = "SmartCampusLog.txt";
    private static final String MOBILE_TELEMETRY_TOPIC_NAME =  "iisc/smartx/mobile/telemetry/data";
    private static final String MOBILE_TELEMETRY_EVENT_NAME = "Mobile Telemetry";
    private static String phoneModel;

    // note: this is not the IMEI  or mac address
    private static String deviceId;
    private static Context applicationContext;
    private static File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);

    public static void initAppContext(Context appContext) {
        if (applicationContext == null) {
            applicationContext = appContext;
        }
    }

    private static void setDeviceId() {
        deviceId = CommonUtils.deviceId(applicationContext);
        if (deviceId == null) {
            deviceId = " ";
        }
    }

    private static void setPhoneModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            phoneModel = model;
        } else {
            phoneModel = manufacturer + "-" + model;
        }
    }

    private static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }

//    public static void doLogging(String loggerData) {
//        String dateString = getCurrentDate();
//        if (phoneModel == null) {
//            setPhoneModel();
//        }
//        if (deviceId == null || " ".equals(deviceId)) {
//            setDeviceId();
//        }
//        String loggerString = phoneModel + "," + deviceId + "," + dateString + "," + loggerData;
//        writeDataToLogFile(loggerString);
//    }

    public static synchronized void writeDataToLogFile(String logString) {
        String dateString = getCurrentDate();
        if (phoneModel == null) {
            setPhoneModel();
        }
        if (deviceId == null || " ".equals(deviceId)) {
            setDeviceId();
        }
        String loggerString = phoneModel + "," + deviceId + "," + dateString + "," + logString;

        if (smartCampusDirectory.exists() == false) {
            if (!smartCampusDirectory.mkdirs()) {
                CommonUtils.printLog("failed to create logfile ");
                return;
            }
        }
        File logFile = new File(smartCampusDirectory, SMART_CAMPUS_LOG_FILE_NAME);
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

    public synchronized static void publishLoggerData(int n) {
        // publish the last n line from the file
        /*
        if n is less than or equal to num file lines then publish all
         */
        int lines = numberofLinesInFile();
        if (lines == -1) {
            return;
        }
        File logFile = new File(smartCampusDirectory, SMART_CAMPUS_LOG_FILE_NAME);
        BufferedReader reader = null;
        MqttPublisher publisher  = new MqttPublisher(MOBILE_TELEMETRY_TOPIC_NAME);
        try {
            reader = new BufferedReader(new FileReader(logFile));
            if (lines > n) {
                for (int i = 0;i< lines-n;i++) {
                    reader.readLine();
                }
            }
            String data;
            while ((data = reader.readLine())!=null) {
                // publish data on a BG thread with a thread sleep of 1 sec
//                publisher.publishData(data);
                CommonUtils.printLog("data published from log file: " + data);
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
            //delete the log file now
            logFile.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int numberofLinesInFile() {
        File logFile = new File(smartCampusDirectory, SMART_CAMPUS_LOG_FILE_NAME);
        int lines = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logFile));
            while (reader.readLine() != null) lines++;
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return lines;
    }
    public static void runStatusPublisher (final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                publishLoggerData(num);
            }
        }).start();
    }
}


