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

    /* MESSAGE HANDLER CONSTANTS */
    int PUBLISH_MESSAGE = 3;
    int SUBSCRIBE_TO_TOPIC = 4;
    int UNSUBSCRIBE_TO_TOPIC = 5;
    int CHECK_SERVICE = 6;
    int CHECK_MQTT_CONNECTION = 7;
    int CONNECT_MQTT = 8;

}
