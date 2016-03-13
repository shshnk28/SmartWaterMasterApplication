package com.example.shashankshekhar.servicedemo.Logger;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

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
import java.util.TimeZone;

/**
 * Created by shashankshekhar on 10/03/16.
 */
public class MqttLogger {
    private static final String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    private static final String SMART_CAMPUS_LOG_FILE_NAME = "SmartCampusLog.txt";
    static private String phoneModel;

    // note: this is not the IMEI  or mac address
    static private String deviceId;
    static private Context applicationContext;
    static private File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);

    private static void initAppContext(Context appContext) {
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

    public static void doLogging(String loggerData) {
        String dateString = getCurrentDate();
        if (phoneModel == null) {
            setPhoneModel();
        }
        if (deviceId == null || " ".equals(deviceId)) {
            setDeviceId();
        }
        String loggerString = phoneModel + "," + deviceId + "," + dateString + "," + loggerData;
        writeDataToLogFile(loggerString);
    }

    private static void writeDataToLogFile(String text) {
        if (smartCampusDirectory.exists() == false) {
            if (!smartCampusDirectory.mkdirs()) {
                CommonUtils.printLog("failed to create logfile ");
                return;
            }
        }
        File logFile = new File(smartCampusDirectory, SMART_CAMPUS_LOG_FILE_NAME);
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            CommonUtils.printLog("file write failed in mqtt logfile");
            return;
        }
    }
    public void publishLoggerData(int n) {
        // publish the last n line from the file
        /*
        if n is less than or equal to num file lines then publish all
         */
        int lines  = numberofLineInFile();
        if (lines == -1) {
            return;
        }
        if (lines <=n) {
            // publish all
        }
        else {
            // goto line num lines-n and from there start publishing
        }
    }
    private int numberofLineInFile() {
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
        } catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return lines;
    }
}


