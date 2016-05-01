package com.example.shashankshekhar.servicedemo.Constants;

import android.provider.BaseColumns;

import com.example.shashankshekhar.servicedemo.DBOperations.SCDBOperations;

/**
 * Created by shashankshekhar on 27/04/16.
 */
public final class DBConstants {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private DBConstants () {} // no instantiation
    public static abstract class SCLogTable implements BaseColumns {
        public static final String TABLE_NAME = "SCLogTable";
        public static final String COL_EVENT_NAME = "Event";
        public static final String COL_TIMESTAMP = "TimeStamp";
        public static final String COL_1 = "Col1";
        public static final String COL_2 = "Col2";
//        public static final String COL_NAME_RESET_TIME = "ResetTime";
    }
//    public static abstract class TopicsSubscribed implements BaseColumns {
//        public static final String TABLE_NAME = "TopicsSubscribed";
//        public static final String COL_NAME_TIMESTAMP = "TimeStamp";
//        public static final String COL_NAME_TOPIC_NAME = "TopicName";
//    }
    public static final String SQL_CREATE_MESSAGE_COUNT = "CREATE TABLE " + SCLogTable.TABLE_NAME + " (" +
            SCLogTable._ID + " INTEGER PRIMARY KEY," +
            SCLogTable.COL_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
//            SCLogTable.COL_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
            SCLogTable.COL_1 + TEXT_TYPE + COMMA_SEP +
            SCLogTable.COL_2 + TEXT_TYPE +
            " )";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + SCLogTable.TABLE_NAME;

    // return count
    public static final String SQL_RETURN_RECEIVED_COUNT = "SELECT COUNT(" + SCLogTable._ID + ") FROM " + SCLogTable
            .TABLE_NAME +" WHERE " + SCLogTable.COL_EVENT_NAME + "=" + "'" + SCDBOperations.MESSAGE_RECEIVED_EVENT_NAME + "'" ;

    public static final String SQL_RETURN_SENT_COUNT = "SELECT COUNT(" + SCLogTable._ID + ") FROM " + SCLogTable
            .TABLE_NAME +" WHERE " + SCLogTable.COL_EVENT_NAME + "=" + "'" + SCDBOperations.MESSAGE_SENT_EVENT_NAME +"'";


    // delete the top row
    public static final String SQL_DELETE_TOP_ROW_RECEIVED = SCLogTable._ID +" IN ( SELECT " + SCLogTable._ID + " FROM " + SCLogTable.TABLE_NAME + " WHERE " + SCLogTable.COL_EVENT_NAME + "=" +"'" + SCDBOperations.MESSAGE_RECEIVED_EVENT_NAME + "'" + " ORDER BY " +
            SCLogTable._ID + " ASC LIMIT 1)";
    /*
    delete from SCLogTable where _id in  (select _id from SCLogTable where Event='Event.MessageReceived' order by _id ASC limit 1)
     */
    public static final String SQL_DELETE_TOP_ROW_SENT = SCLogTable._ID +" IN ( " + "SELECT "+ SCLogTable._ID +
            " FROM "  + SCLogTable.TABLE_NAME + " WHERE " + SCLogTable.COL_EVENT_NAME + "=" + "'" + SCDBOperations.MESSAGE_SENT_EVENT_NAME +"'"+ " ORDER BY " + SCLogTable
            ._ID + " ASC LIMIT 1)";

    public static final String SQL_CREATE_MESSAEG_COUNT_ROW = "INSERT INTO " + SCLogTable.TABLE_NAME +" ("+SCLogTable
            .COL_EVENT_NAME + COMMA_SEP + SCLogTable.COL_1 + COMMA_SEP + SCLogTable.COL_2+ ") " +
            "VALUES" + " (" + "'" +SCDBOperations.MESSAGE_COUNT_EVENT_NAME+"'" + COMMA_SEP +"'"+ SCDBOperations
            .SENT_MESSAGE_COUNT + ":0" +"'"+
            COMMA_SEP + "'" +SCDBOperations.RECEIVED_MESSAGE_COUNT + ":0" +  "')";

    // return the top events
    public static final String SQL_RETURN_SENT_EVENTS = "SELECT " + SCLogTable.COL_1 + COMMA_SEP + SCLogTable
            .COL_2 + " FROM " + SCLogTable.TABLE_NAME  + " WHERE " + SCLogTable.COL_EVENT_NAME + "=" + "'" +
            SCDBOperations.MESSAGE_SENT_EVENT_NAME + "'";

    public static final String SQL_RETURN_RECEIVED_EVENTS = "SELECT " + SCLogTable.COL_1 + COMMA_SEP + SCLogTable
            .COL_2 + " FROM " + SCLogTable.TABLE_NAME  + " WHERE " + SCLogTable.COL_EVENT_NAME + "=" + "'" +
            SCDBOperations.MESSAGE_RECEIVED_EVENT_NAME + "'";

}
