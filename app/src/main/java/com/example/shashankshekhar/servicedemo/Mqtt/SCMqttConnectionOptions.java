package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by shashankshekhar on 09/11/15.
 * set the connection options here
 */
// TODO: 10/11/15 make it singleton ?. what if we need different username and password for some connections  
public class SCMqttConnectionOptions implements MQTTConstants {
    public static MqttConnectOptions getConnectionOptions () {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(Boolean.getBoolean(ConnOptsJsonHandler.readFromJsonFile(CLEAN_SESSION_KEY)));
        connOpts.setUserName(ConnOptsJsonHandler.readFromJsonFile(USER_NAME_KEY));
        connOpts.setPassword(ConnOptsJsonHandler.readFromJsonFile(PASSWORD_KEY).toString().toCharArray());
        int connectionTO = Integer.parseInt(ConnOptsJsonHandler.readFromJsonFile(CONNECTION_TIME_OUT_KEY));
        connOpts.setConnectionTimeout(connectionTO);
        int keepAlive  = Integer.parseInt(ConnOptsJsonHandler.readFromJsonFile(KEEP_ALIVE_KEY));
        keepAlive*=60;
        connOpts.setKeepAliveInterval(keepAlive);
//        printConnectionProperties();
        return connOpts;
    }
}
