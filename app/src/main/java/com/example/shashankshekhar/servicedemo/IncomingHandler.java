package com.example.shashankshekhar.servicedemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 30/03/16.
 */
public class IncomingHandler extends Handler {
//    static final int MQTT_CONNECTED =1;
//    static final int UNABLE_TO_CONNECT =2;
//    static final int NO_NETWORK_AVAILABLE =4;
//    static final int MQTT_CONNECTION_IN_PROGRESS = 5;
//    static final int MQTT_NOT_CONNECTED = 6;
//
//    static final int TOPIC_PUBLISHED = 7;
//    static final int ERROR_IN_PUBLISHING = 8;

    Context applicationContext;
    ServiceCallback callback;
    public IncomingHandler(Context context,ServiceCallback callback1) {
        applicationContext = context;
        callback = callback1;
    }
    @Override
    public void handleMessage (Message message) {
        callback.messageReceivedFromService(message.what);
    }
}
