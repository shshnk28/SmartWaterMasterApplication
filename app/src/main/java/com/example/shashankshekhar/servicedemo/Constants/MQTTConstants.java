package com.example.shashankshekhar.servicedemo.Constants;

/**
 * Created by shashankshekhar on 06/11/15.
 */
public interface MQTTConstants {
    /* CONNECTION CONSTANTS */
    String CLIENT_ID = "shashank";
    int QoS = 1;
    String BROKER_ADDRESS_CLOUD = "tcp://smartx.cloudapp.net:1883";
    String USERNAME = "admin";
    String PASSWORD = "password";

    /* MESSAGE HANDLER CONSTANTS */
    int PUBLISH_MESSAGE = 3;
    int SUBSCRIBE_TO_TOPIC = 4;
    int UNSUBSCRIBE_TO_TOPIC = 5;
    int CHECK_SERVICE = 6;
    int CHECK_MQTT_CONNECTION = 7;
    int CONNECT_MQTT = 8;



}
