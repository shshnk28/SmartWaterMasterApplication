package com.example.shashankshekhar.servicedemo.DBOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.shashankshekhar.servicedemo.Constants.DBConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 27/04/16.
 */
public class SCDBOperations {
    static Context appContext;
    private static String MESSAGE_RECEIVED_EVENT_NAME = "Event.MessageReceived";
    private static String MESSAGE_SENT_EVENT_NAME = "Event.MessageSent";
    private static String TOPIC = "topic";
    private static String PAYLOAD = "payload";

    public static void initDBAppContext (Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }
    private static void incrementMessgeSentCount (int incr) {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
    }
    private static void incrementMessgeReceivedCount (int incr) {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
    }
    public static void messageSent (String topicName, String payload) {
        doMessageSentOrReceivedEntry(true, topicName, payload);
    }

    public static void messageReceived (String topicName, String payload) {
        doMessageSentOrReceivedEntry(false,topicName,payload);
    }
    private static void doMessageSentOrReceivedEntry (boolean isSent, String topicName,String payload) {

        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (isSent) {
            values.put(DBConstants.SCLogTable.COL_EVENT_NAME, MESSAGE_SENT_EVENT_NAME);
            // increment the sent message count
        } else {
            values.put(DBConstants.SCLogTable.COL_EVENT_NAME, MESSAGE_RECEIVED_EVENT_NAME);
            // increment the received message count
        }
        values.put(DBConstants.SCLogTable.COL_1, TOPIC + ":"+ topicName);
        values.put(DBConstants.SCLogTable.COL_2, PAYLOAD + ":"+ payload);
        long newRowId;
        newRowId = db.insert(
                DBConstants.SCLogTable.TABLE_NAME,
                null,
                values
        );
        // by: java.lang.IllegalStateException: SQLiteDatabase created and never closed check if you get this if yes
        // then close the goddamn db.
    }
//    public static void writeToDB () {
//        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DBConstants.SCLogTable.COL_NAME_MESSAGE_RECEIVED, "11");
//        values.put(DBConstants.SCLogTable.COL_NAME_MESSAGE_SENT, "22");
//        long newRowId;
//        newRowId = db.insert(
//                DBConstants.SCLogTable.TABLE_NAME,
//                null,
//                values
//        );
//        CommonUtils.printLog("the db path is: "+db.getPath() + " row id: "+ newRowId);
//        dbHelper.exportDatabse(appContext);
//    }
}
