package com.example.shashankshekhar.servicedemo.DBOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.shashankshekhar.servicedemo.Constants.DBConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shashankshekhar on 27/04/16.
 */
public class SCDBOperations {
    static ExecutorService executor = Executors.newSingleThreadExecutor();
    static Context appContext;
    public static String MESSAGE_RECEIVED_EVENT_NAME = "Event.MessageReceived";
    public static String MESSAGE_SENT_EVENT_NAME = "Event.MessageSent";
    public static String TOPIC_SUBSCRIBED_EVENT_NAME = "Event.TopicSubscribed";
    public static String MESSAGE_COUNT_EVENT_NAME = "Event.MessageCount";
    private static String TOPIC = "topic";
    private static String PAYLOAD = "payload";
    public static String SENT_MESSAGE_COUNT = "SentMessageCount";
    public static String RECEIVED_MESSAGE_COUNT = "ReceivedMessageCount";
    // store this many last  received and sent messages
    public static final int STORAGE_NUM = 10;

    public static void initDBAppContext(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }

    private static void incrementMessgeSentCount(int incr) {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
    }

    private static void incrementMessgeReceivedCount(int incr) {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
    }

    public static void messageSent(String topicName, String payload) {
        doMessageSentOrReceivedEntry(true, topicName, payload);
    }

    public static void messageReceived(String topicName, String payload) {
        doMessageSentOrReceivedEntry(false, topicName, payload);
    }

    private static void doMessageSentOrReceivedEntry(final boolean isSent, final String topicName, final String payload) {
        /*
        better approach is to have a queue and put operation in a queue. this is spawning a new thread for each
        operation
         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
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
                values.put(DBConstants.SCLogTable.COL_1, TOPIC + ":" + topicName);
                values.put(DBConstants.SCLogTable.COL_2, PAYLOAD + ":" + payload);

                long newRowId;
                newRowId = db.insert(
                        DBConstants.SCLogTable.TABLE_NAME,
                        null,
                        values
                );
                deleteExtraMessages(isSent, db);
                int[] msgCont = returnCurrentMessageCount();
                values.clear();
                if (isSent){
                    values.put(DBConstants.SCLogTable.COL_1,SENT_MESSAGE_COUNT + ":" + (msgCont[0]+1));
                } else {
                    values.put(DBConstants.SCLogTable.COL_2,RECEIVED_MESSAGE_COUNT+ ":" + (msgCont[1]+1));
                }
                db.update(DBConstants.SCLogTable.TABLE_NAME,
                        values,
                        DBConstants.SCLogTable.COL_EVENT_NAME + "=?",
                        new String[] {MESSAGE_COUNT_EVENT_NAME});

            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();

        // by: java.lang.IllegalStateException: SQLiteDatabase created and never closed check if you get this if yes
        // then close the goddamn db.
    }

    /*
    this method keeps the number of sent and received messages to a set number
     */
    private static void deleteExtraMessages(boolean isSent, SQLiteDatabase db) {
        String countQuery = null;
        String deleteQuery = null;
        if (isSent) {
            countQuery = DBConstants.SQL_RETURN_SENT_COUNT;
            deleteQuery = DBConstants.SQL_DELETE_TOP_ROW_SENT;
        } else {
            countQuery = DBConstants.SQL_RETURN_RECEIVED_COUNT;
            deleteQuery = DBConstants.SQL_DELETE_TOP_ROW_RECEIVED;
        }
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        while (count > STORAGE_NUM) {
//                        Cursor cursor1 = db.rawQuery(DBConstants.SQL_DELETE_TOP_ROW_SENT, null);
            db.delete(DBConstants.SCLogTable.TABLE_NAME, deleteQuery, null);
            count--;
            if (isSent) {
                CommonUtils.printLog("sent deleted");
            } else {
                CommonUtils.printLog("received deleted");
            }
        }
    }

    public static Cursor returnReceivedMessaegDataFromDB() {
        return readMessagesFromDB(false);
    }

    public static Cursor returnSentMessageDataFromDB() {
        return readMessagesFromDB(true);
    }

    private static Cursor readMessagesFromDB(boolean isSent) {
        String messageType;
        if (isSent) {
            messageType = MESSAGE_SENT_EVENT_NAME;
        } else {
            messageType = MESSAGE_RECEIVED_EVENT_NAME;
        }
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBConstants.SCLogTable.TABLE_NAME, new String[]{DBConstants.SCLogTable._ID, DBConstants.SCLogTable.COL_1, DBConstants.SCLogTable.COL_2}, DBConstants.SCLogTable.COL_EVENT_NAME + "=?", new String[]{messageType}, null, null,
                DBConstants.SCLogTable._ID + " DESC ", null);
        return cursor;
    }

    public static void addSubscribedTopicToDB(final String topicName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBConstants.SCLogTable.COL_EVENT_NAME, TOPIC_SUBSCRIBED_EVENT_NAME);
                values.put(DBConstants.SCLogTable.COL_1, TOPIC + ":" + topicName);
                values.put(DBConstants.SCLogTable.COL_2, PAYLOAD + ":" + "0");

                long newRowId;
                newRowId = db.insert(
                        DBConstants.SCLogTable.TABLE_NAME,
                        null,
                        values
                );
            }
        });

    }

    public static void removeUnsubscribedTopicFromDB(final String topicName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int rows = db.delete(DBConstants.SCLogTable.TABLE_NAME,
                        DBConstants.SCLogTable.COL_EVENT_NAME + "= ? AND " + DBConstants.SCLogTable.COL_1 + "=?",
                        new String[]{TOPIC_SUBSCRIBED_EVENT_NAME, TOPIC + ":" + topicName});
                CommonUtils.printLog("rows deleted: " + rows);
            }
        });

    }

    public static Cursor returnSubscribedTopicsFromDB() {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBConstants.SCLogTable.TABLE_NAME, new String[]{DBConstants.SCLogTable._ID, DBConstants.SCLogTable.COL_1}, DBConstants.SCLogTable.COL_EVENT_NAME + "=?", new String[]{TOPIC_SUBSCRIBED_EVENT_NAME}, null, null,
                DBConstants.SCLogTable._ID + " DESC ", null);
        return cursor;
    }

    public static int[] returnCurrentMessageCount() {
        SCDBHelper dbHelper = SCDBHelper.gethelperInstance(appContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBConstants.SCLogTable.TABLE_NAME, new String[]{DBConstants.SCLogTable._ID, DBConstants.SCLogTable.COL_1, DBConstants.SCLogTable.COL_2}, DBConstants.SCLogTable.COL_EVENT_NAME + "=?", new
                        String[]{MESSAGE_COUNT_EVENT_NAME},
                null, null, null, null);
        int sentMsgs =0, receivedMsgs = 0;
        if (cursor.moveToFirst()) {
            String[] sentMessages = cursor.getString(1).split(":");
            sentMsgs= Integer.parseInt(sentMessages[1]);
            String[] receivedMessges = cursor.getString(2).split(":");
            receivedMsgs = Integer.parseInt(receivedMessges[1]);
        }
        int[] result = {sentMsgs,receivedMsgs};
        return result;
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
