package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class GcmMessageHandler extends com.google.android.gms.gcm.GcmListenerService {
    private static final String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    private static final String SMART_CAMPUS_LOG_FILE_NAME2 = "FromPubThread.txt";
    private static File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);

    public GcmMessageHandler() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        CommonUtils.printLog("Great Success.. message received from gcm..");
        writeDataToLogFile("gcm notif received");
    }

    //    @Override
//    public void onTokenRefresh() {
//        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        Intent intent = new Intent(this, MyInstanceIDService.class);
//        startService(intent);
//    }
    public void writeDataToLogFile(String logString) {
        String userName = "Shashank";
        String dateString = getCurrentDate();
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

    private static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }

}
