package com.example.shashankshekhar.servicedemo.Constants;

/**
 * Created by shashankshekhar on 06/11/15.
 */
public interface MQTTConstants {
    /* CONNECTION CONSTANTS */
//    String CLIENT_ID = "someRandomPerson";
    int QoS = 1;
    String BROKER_ADDRESS_CLOUD = "tcp://smartx.cds.iisc.ac.in:1883";
    String STATIC_BROKER_ADDRESS = "tcp://13.76.132.113:1883";
    String GOOGLE_INDIA = "www.google.co.in";
    String GOOGLE_INDIA_NO_DNS = "8:8:8:8";


    String USERNAME = "AppUser";
    String PASSWORD = "scdl@App";

    String PACKAGE_NAME = "com.example.shashankshekhar.servicedemo";
    String SERVICE_NAME = "com.example.shashankshekhar.servicedemo.FirstService";

//    String PREFS_NAME = "MyPrefsFile";
//    String USER_NAME_KEY = "username";

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

    /* these are returned from service as a a response to the above requests. Needs to be in the library once it is
    integrated.*/
    // connection status
    int MQTT_CONNECTED =1;
    int UNABLE_TO_CONNECT =2;
    int NO_NETWORK_AVAILABLE =4;
    int MQTT_CONNECTION_IN_PROGRESS = 5;
    int MQTT_NOT_CONNECTED = 6;

    // publish status
    int TOPIC_PUBLISHED = 7;
    int ERROR_IN_PUBLISHING = 8;

    // subscription status
    int SUBSCRIPTION_SUCCESS = 9;
    int SUBSCRIPTION_ERROR = 10;

}
