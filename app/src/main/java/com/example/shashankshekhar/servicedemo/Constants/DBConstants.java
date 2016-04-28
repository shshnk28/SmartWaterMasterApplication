package com.example.shashankshekhar.servicedemo.Constants;

import android.provider.BaseColumns;

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
    public static abstract class TopicsSubscribed implements BaseColumns {
        public static final String TABLE_NAME = "TopicsSubscribed";
        public static final String COL_NAME_TIMESTAMP = "TimeStamp";
        public static final String COL_NAME_TOPIC_NAME = "TopicName";
    }
    public static final String SQL_CREATE_MESSAGE_COUNT = "CREATE TABLE " + SCLogTable.TABLE_NAME + " (" +
            SCLogTable._ID + " INTEGER PRIMARY KEY," +
            SCLogTable.COL_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
//            SCLogTable.COL_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
            SCLogTable.COL_1 + TEXT_TYPE + COMMA_SEP +
            SCLogTable.COL_2 + TEXT_TYPE + COMMA_SEP +
            " )";
    private static final String SQL_DELETE_MESSAGE_COUNT =
            "DROP TABLE IF EXISTS " + SCLogTable.TABLE_NAME;
}
