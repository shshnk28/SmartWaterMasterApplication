package com.example.shashankshekhar.servicedemo.Constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by shashankshekhar on 06/11/15.
 */
public interface MQTTConstants {
    /* CONNECTION CONSTANTS */
    int QoS = 1;
    String STATIC_BROKER_ADDRESS = "tcp://13.76.132.113:1883";
    String GOOGLE_INDIA = "www.google.co.in";
    String GOOGLE_INDIA_NO_DNS = "8:8:8:8";


    // JSON Connection Option keys
    String BROKER_ADDRESS_KEY = "brokerAddress";
    String PORT_NUM_KEY = "portNumber";
    String USER_NAME_KEY = "userName";
    String PASSWORD_KEY = "password";
    String CONNECTION_TIME_OUT_KEY = "connectionTimeOut";
    String PING_FREQ_KEY = "pingFreq";
    String KEEP_ALIVE_KEY = "keepALive";
    String CLEAN_SESSION_KEY = "cleanSession";
    String SSL_ENABLED_KEY = "SslEnabled";
    String PUBLISH_CONN_LOGS_KEY = "publishConnLogs";

    // JSON Connection Option values
    String BROKER_ADDRESS_VAL = "tcp://smartx.cds.iisc.ac.in";
    String PORT_NUM_VAL = "1883";
    String USER_NAME_VAL = "AppUser";
    String PASSWORD_VAL = "scdl@App";
    int CONNECTION_TIMEOUT_VAL = 120; //seconds
    int KEEP_ALIVE_INTERVAL_VAL = 20; // minutes
    int PING_FREQ_VAL = 3; // minutes
    boolean CLEAN_SESSION_VAL = false;
    boolean SSL_ENABLED_VAL = false;
    boolean PUBLISH_CONN_LOGS_VAL = false;


    String PACKAGE_NAME = "com.example.shashankshekhar.servicedemo";
    String SERVICE_NAME = "com.example.shashankshekhar.servicedemo.FirstService";


    String SMART_CAMPUS_FOLDER_NAME = "SmartCampus";
    File smartCampusDirectory = new File(Environment.getExternalStorageDirectory(), SMART_CAMPUS_FOLDER_NAME);

    String PREFS_NAME = "MyPrefsFile";
//    String USER_NAME_VAL = "username";

    String TEST_TOPIC = "iisc/smartx/crowd/network/mqttTest";
    String TEST_TOPIC1 = "iisc/smartx/crowd/network/mqttTest1";


    // TODO: 31/03/16 PUT THESE IN THE library once integrated
    /* MESSAGE HANDLER CONSTANTS. used when sending message to service to perform some action.Needs to be in the library once it is
    integrated. */
    int PUBLISH_MESSAGE = 3;
    int SUBSCRIBE_TO_TOPIC = 4;
    int UNSUBSCRIBE_TO_TOPIC = 5;
    int CHECK_SERVICE = 6;
    int CHECK_MQTT_CONNECTION = 7;
    int CONNECT_MQTT = 8;
    int DISCONNECT_MQTT = 9;

    /* these are returned from service as a a response to the above requests. Needs to be in the library once it is
    integrated.*/

    // connection status
    int MQTT_CONNECTED =1;
    int UNABLE_TO_CONNECT =2;
    int NO_NETWORK_AVAILABLE =4;
    int MQTT_CONNECTION_IN_PROGRESS = 5;
    int MQTT_NOT_CONNECTED = 6;
    int DISCONNECT_SUCCESS= 11;

    // publish status
    int TOPIC_PUBLISHED = 7;
    int ERROR_IN_PUBLISHING = 8;

    // subscription status
    int SUBSCRIPTION_SUCCESS = 9;
    int SUBSCRIPTION_ERROR = 10;

}
