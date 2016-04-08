package com.example.shashankshekhar.servicedemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 08/04/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TEST_TOPIC = "iisc/smartx/crowd/network/ping";
    MqttPublisher publisher;
    public void onReceive(Context context, Intent intent) {
    //alarm received here
        CommonUtils.printLog("alarm received");
        if (publisher == null) {
            publisher = new MqttPublisher(TEST_TOPIC);
        }
        publisher.publishData("0");
    }
}
