package com.example.shashankshekhar.servicedemo.Mqtt;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by shashankshekhar on 09/11/15.
 * set the connection options here
 */
// TODO: 10/11/15 make it singleton ?. what if we need different username and password for some connections  
public class SCMqttConnectionOptions implements MQTTConstants {
    private static MqttConnectOptions connOpts = null;
    private static void initialiseConnectionOptions () {
        connOpts = new MqttConnectOptions();
        // TODO: 29/02/16 set the clean session to false if you want to get resubscribed to the old topics   
        connOpts.setCleanSession(false);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        connOpts.setConnectionTimeout(60);
        connOpts.setKeepAliveInterval(20*60);
    }
    public static MqttConnectOptions getConnectionOptions () {
        if (connOpts == null) {
            initialiseConnectionOptions();
        }
//        printConnectionProperties();
        return connOpts;
    }
    public static  void printConnectionProperties () {
        if (connOpts == null) {
            CommonUtils.printLog("connOpts is null returning");
            return;
        }
        int i = connOpts.getKeepAliveInterval();
        CommonUtils.printLog("keep alive interval = " + Integer.toString(i));
        String[] k = connOpts.getServerURIs();
        if ( k!= null &&  k.length != 0) {
            for (String str:k) {
                CommonUtils.printLog("server URI: " + k);
            }
        }
        int mqttVersion = connOpts.getMqttVersion();
        CommonUtils.printLog("get version : " + Integer.toString(mqttVersion));
        String willDestinaion = connOpts.getWillDestination();
        CommonUtils.printLog("will destination" + willDestinaion);
    }
}
