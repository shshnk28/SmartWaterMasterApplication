package com.example.shashankshekhar.servicedemo.DBOperations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.shashankshekhar.servicedemo.Constants.DBConstants;
import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by shashankshekhar on 27/04/16.
 */
public class SCDBHelper extends SQLiteOpenHelper implements MQTTConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SmartCampusMobileDB.db";
    private static SCDBHelper scdbHelper;

    private SCDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SCDBHelper gethelperInstance(Context context) {
        if (scdbHelper != null) {
            return scdbHelper;
        }
        new SCDBHelper(context);
        return scdbHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.SQL_CREATE_MESSAGE_COUNT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DBConstants.SQL_CREATE_MESSAGE_COUNT);
        onCreate(db);
    }

    public void exportDatabse(Context context) {
        try {
            File data = Environment.getDataDirectory();
            String currentDBPath = "//data//"+ context.getPackageName()+"//databases//"+DATABASE_NAME+"";
            String backupDBPath = "SmartCampusDB.db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(smartCampusDirectory, backupDBPath);
            if (backupDB.exists()) {
                backupDB.delete();
            }
            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } else {
                CommonUtils.printLog("current db does not exist");
            }
        } catch (Exception e) {
            CommonUtils.printLog("exception in exporting db");
        }
    }
}
