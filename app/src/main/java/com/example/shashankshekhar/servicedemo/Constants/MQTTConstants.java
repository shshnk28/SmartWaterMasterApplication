package com.example.shashankshekhar.servicedemo.Constants;

/**
 * Created by shashankshekhar on 06/11/15.
 */
public interface MQTTConstants {
    String CLIENT_ID = "shashank";
    int QoS = 1;
    String BROKER_ADDRESS_LOCAL = "tcp://10.16.37.222:61613";
    String BROKER_ADDRESS_CLOUD = "tcp://smartx.cloudapp.net:1883";
    String BROKER_ADDRESS_TEST = "tcp://iot.eclipse.org:1883";
    String USERNAME = "admin";
    String PASSWORD = "password";
}
