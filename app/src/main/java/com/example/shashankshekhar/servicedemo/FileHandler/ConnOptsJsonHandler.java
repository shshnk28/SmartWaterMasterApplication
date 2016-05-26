package com.example.shashankshekhar.servicedemo.FileHandler;

import android.util.JsonWriter;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by shashankshekhar on 18/04/16.
 */
public class ConnOptsJsonHandler implements MQTTConstants {
    private static JsonWriter jsonWriter;
    private static final String JSON_FILE = "configSettings.json";
    private static File configFile= new File(smartCampusDirectory, JSON_FILE);
    public static void initJsonWriter () {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(configFile);
            jsonWriter = new JsonWriter(new OutputStreamWriter(stream));
            jsonWriter.beginObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public static void closeJsonFile () {
        try {
            jsonWriter.endObject();
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void writeToJsonFile (String key, String value) {
        try {
            jsonWriter.name(key).value(value);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void writeDefaultConnectionSettings () {
        // return if it exists
        if (configFile.exists()) {
            CommonUtils.printLog("json exists returning");
            return;
        }
        initJsonWriter();
        writeToJsonFile(BROKER_ADDRESS_KEY, BROKER_ADDRESS_VAL);
        writeToJsonFile(PORT_NUM_KEY, PORT_NUM_VAL);
        writeToJsonFile(USER_NAME_KEY, USER_NAME_VAL);
        writeToJsonFile(PASSWORD_KEY, PASSWORD_VAL);
        writeToJsonFile(CONNECTION_TIME_OUT_KEY,Integer.toString(CONNECTION_TIMEOUT_VAL));
        writeToJsonFile(PING_FREQ_KEY,Integer.toString(PING_FREQ_VAL));
        writeToJsonFile(KEEP_ALIVE_KEY,Integer.toString(KEEP_ALIVE_INTERVAL_VAL));
        writeToJsonFile(CLEAN_SESSION_KEY,Boolean.toString(CLEAN_SESSION_VAL));
        writeToJsonFile(SSL_ENABLED_KEY,Boolean.toString(SSL_ENABLED_VAL));
        writeToJsonFile(PUBLISH_CONN_LOGS_KEY,Boolean.toString(PUBLISH_CONN_LOGS_VAL));
        closeJsonFile();
        CommonUtils.printLog("default json created");
    }

    public static String readFromJsonFile (String key) {
        try {
            InputStream inputStream = new FileInputStream(configFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getString(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static boolean doesConfigFileExists () {
        return configFile.exists();
    }
    public static String jsonPath () {
        if (configFile.exists() == false) {
            return "file does not exist";
        }
        return configFile.getAbsolutePath();
    }
}
